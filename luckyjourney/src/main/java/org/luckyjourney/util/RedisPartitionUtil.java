package org.luckyjourney.util;

import org.luckyjourney.constant.RedisConstant;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.OptionalLong;

/**
 * Redis key 分片工具：按照日期对分类视频集合做切分，控制单个 Set 的体量。
 * <p>默认采用「系统时区 + yyyyMMdd」的粒度，同时兼容旧有数据。</p>
 */
public final class RedisPartitionUtil {

    /** 使用紧凑的 yyyyMMdd 格式表示分片日期 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    /** 允许根据部署所在时区生成分片，避免日期错位 */
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    /** 默认只保留最近 7 天分片，过期数据可按需扩展 */
    private static final int DEFAULT_TYPE_PARTITION_WINDOW_DAYS = 7;

    private RedisPartitionUtil() {
    }

    /**
     * 按毫秒时间戳生成指定分类的分片 key。
     */
    public static String typeStockKey(Long typeId, long timestampMillis) {
        LocalDate day = Instant.ofEpochMilli(timestampMillis).atZone(ZONE_ID).toLocalDate();
        return RedisConstant.SYSTEM_TYPE_STOCK + typeId + ":" + DATE_FORMATTER.format(day);
    }

    /**
     * 直接基于 LocalDate 生成分片 key，便于测试或手动维护。
     */
    public static String typeStockKey(Long typeId, LocalDate day) {
        return RedisConstant.SYSTEM_TYPE_STOCK + typeId + ":" + DATE_FORMATTER.format(day);
    }

    /**
     * 基于视频 ID（雪花算法）推导出分片时间，若解析失败则回退到创建时间/当前时间。
     */
    public static String resolveTypeStockKey(Long typeId, Long videoId, Date createdAt) {
        long fallbackTimestamp = createdAt != null ? createdAt.getTime() : System.currentTimeMillis();
        OptionalLong timestamp = videoId == null ? OptionalLong.empty() : SnowflakeIdGenerator.tryExtractTimestamp(videoId);
        long effectiveTs = timestamp.orElse(fallbackTimestamp);
        return typeStockKey(typeId, effectiveTs);
    }

    /**
     * 生成默认范围内（7 天）的分片 key 列表，供读取时批量抽样。
     */
    public static List<String> recentTypeStockKeys(Long typeId) {
        return recentTypeStockKeys(typeId, DEFAULT_TYPE_PARTITION_WINDOW_DAYS);
    }

    /**
     * 按照指定历史天数生成分片 key，支持调大窗口用于迁移或回溯。
     */
    public static List<String> recentTypeStockKeys(Long typeId, int days) {
        LocalDate today = LocalDate.now(ZONE_ID);
        List<String> keys = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            keys.add(typeStockKey(typeId, today.minusDays(i)));
        }
        return keys;
    }
}
