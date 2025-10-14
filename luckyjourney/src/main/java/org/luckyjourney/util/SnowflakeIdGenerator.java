package org.luckyjourney.util;

import java.util.OptionalLong;

/**
 * 使用简化版 Snowflake 算法生成 64 位长整型 ID。
 * <p>结构为「时间戳 | 数据中心 | 工作节点 | 序列号」，保证趋势递增且全局唯一，
 * 同时支持从生成的 ID 中还原时间戳，便于后续按照时间维度做分片。</p>
 */
public final class SnowflakeIdGenerator {

    /**
     * 自定义纪元（毫秒），用于缩短时间位占用：2024-01-01 00:00:00 UTC。
     */
    private static final long EPOCH;

    /** 工作节点与数据中心位数配置，与经典 Snowflake 保持一致 */
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 工作节点与数据中心的编号，优先读取 JVM 属性，其次读取环境变量，默认 0。
     * 配置项：luckyjourney.workerId / LUCKYJOURNEY_WORKER_ID。
     * 配置项：luckyjourney.datacenterId / LUCKYJOURNEY_DATACENTER_ID。
     */
    private static final long WORKER_ID;
    private static final long DATACENTER_ID;

    /** 同一毫秒内的序列号以及上一毫秒的时间戳缓存 */
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    static {
        EPOCH = 1704067200000L;
        WORKER_ID = resolveId("luckyjourney.workerId", "LUCKYJOURNEY_WORKER_ID", MAX_WORKER_ID);
        DATACENTER_ID = resolveId("luckyjourney.datacenterId", "LUCKYJOURNEY_DATACENTER_ID", MAX_DATACENTER_ID);
    }

    private SnowflakeIdGenerator() {
    }

    /**
     * 生成下一个 ID，线程安全；若同一毫秒内请求溢出，则等待到下一毫秒。
     */
    public static synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("系统时钟回拨，无法生成 ID，回拨毫秒数：" + (lastTimestamp - timestamp));
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (DATACENTER_ID << DATACENTER_ID_SHIFT)
                | (WORKER_ID << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 尝试从生成的 ID 中解析出真实时间戳，便于 Redis 分片按时间命名。
     */
    public static OptionalLong tryExtractTimestamp(long id) {
        if (id <= 0) {
            return OptionalLong.empty();
        }
        long timestampPart = id >> TIMESTAMP_LEFT_SHIFT;
        if (timestampPart <= 0) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(timestampPart + EPOCH);
    }

    /**
     * 自旋等待到下一毫秒，确保序列号回滚后仍能生成唯一 ID。
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /** 当前系统时间，拆分为单独方法方便未来替换为 NTP 时间源。 */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 读取 Worker/Datacenter 配置，先查 JVM 属性再查环境变量，若为空则返回 0。
     */
    private static long resolveId(String systemPropertyKey, String envKey, long max) {
        String raw = System.getProperty(systemPropertyKey);
        if (raw == null || raw.isEmpty()) {
            raw = System.getenv(envKey);
        }
        if (raw == null || raw.isEmpty()) {
            return 0L;
        }
        long value = Long.parseLong(raw);
        if (value < 0 || value > max) {
            throw new IllegalArgumentException("Snowflake 配置超出范围: " + systemPropertyKey + "/" + envKey);
        }
        return value;
    }

    public static long getEpoch() {
        return EPOCH;
    }

    public static long getTimestampLeftShift() {
        return TIMESTAMP_LEFT_SHIFT;
    }
}
