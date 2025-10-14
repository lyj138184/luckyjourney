# 分片 Key 改造实现思路

## 背景与目标
- 原始实现将所有某分类下的视频 ID 写入同一个 Redis Set，例如 `system:type:stock:12`，热门分类容易形成大 Key，影响性能与可用性。
- 升级目标：引入趋势递增的全局唯一视频 ID，并对分类集合按时间分片，限制单 Key 体量，同时兼顾历史数据回溯。

## 核心步骤

### 1. 雪花算法 ID 生成器
- 新增 `SnowflakeIdGenerator`，自定义 epoch（UTC 2024-01-01），按 `[timestamp | datacenter | worker | sequence]` 组合 64 位 ID。
- Worker/Datacenter 通过系统属性 `luckyjourney.workerId`、`luckyjourney.datacenterId` 或环境变量注入，默认 0。
- 提供 `nextId()` 生成趋势递增 ID，并支持 `tryExtractTimestamp(id)` 反解时间戳，用作分片依据。

### 2. 时间分片工具
- 新增 `RedisPartitionUtil`，封装分片 key 逻辑：`system:type:stock:{typeId}:{yyyyMMdd}`，默认窗口 7 天。
- 关键方法：
  - `resolveTypeStockKey(typeId, videoId, createdAt)`：优先从雪花 ID 解析时间，否则回退到入库时间或当前时间。
  - `recentTypeStockKeys(typeId, days)`：构造最近 N 天的分片 key 列表，用于批量抽样。

### 3. 改造业务读写
- 发布/审核写入：`pushSystemTypeStockIn` 使用 `RedisPartitionUtil.resolveTypeStockKey` 写入分片 Set。
- 读取推荐：`listVideoIdByTypeId` 先遍历最近 7 天分片执行 `SRANDMEMBER`，不足时再从旧 key 补足，确保平滑迁移。
- 下架清理：`deleteSystemTypeStockIn` 同时移除分片 key 与旧 key 中的数据，避免脏数据残留。

## 注意事项
- 数据库仍使用自增 ID；当前改造仅在业务层覆盖新增视频的 ID，若需彻底迁移需同步数据库主键策略。
- 分片窗口可根据需求调整，必要时可扩展成按月/周切分。
- 线上需监控新分片 key 数量及命中率，并逐步淘汰旧的 `system:type:stock:{typeId}` 集合。
- Snowflake ID 依赖系统时钟，需保障服务器时间同步，防止回拨导致异常。
