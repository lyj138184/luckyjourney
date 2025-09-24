好的，根据对所提供代码的详细分析，`luckyjourney` 项目中主要使用了以下几种 Redis 数据结构：

### 1. String (字符串)
String 类型用于存储简单的键值对，常用于计数器、临时数据缓存和访问频率限制等场景。

*   **键名格式:** `email:code:{email}`
    *   **用途:** 存储用户注册或找回密码时的邮箱验证码。
    *   **使用位置:** `CaptchaServiceImpl`, `LoginServiceImpl`
*   **键名格式:** `history:video:{videoId}:{userId}`
    *   **用途:** 作为去重标记，记录用户是否在短时间内浏览过某个视频，以避免重复计算播放量。
    *   **使用位置:** `VideoServiceImpl`
*   **键名格式:** `video:limit:{userId}`
    *   **用途:** 用于限制用户发布视频的频率。通过递增计数值，在一小时内限制发布次数。
    *   **使用位置:** `LimiterAop`

### 2. Hash (哈希)
Hash 用于存储结构化数据，例如一个对象的多个字段。这使得可以高效地读取或修改对象的单个属性，而无需获取整个对象。

*   **键名格式:** `user:model:{userId}`
    *   **用途:** 存储用户的兴趣模型。Hash 的字段 (field) 是视频标签 (label)，值 (value) 是代表用户对该标签兴趣度的分数。
    *   **使用位置:** `InterestPushServiceImpl`, `UserServiceImpl`

### 3. Set (集合)
Set 用于存储一组唯一的、无序的元素。非常适合用于管理标签下的视频ID集合，或构建推荐内容的视频池。

*   **键名格式:** `system:stock:{labelName}`
    *   **用途:** 存储带有特定标签 (label) 的所有视频ID。这使得系统可以根据标签快速随机地推荐视频。
    *   **使用位置:** `InterestPushServiceImpl`, `VideoServiceImpl`
*   **键名格式:** `system:type:stock:{typeId}`
    *   **用途:** 存储属于某个特定分类下的所有视频ID。
    *   **使用位置:** `InterestPushServiceImpl`, `VideoServiceImpl`
*   **键名格式:** `hot:video:{date}`
    *   **用途:** 存储在特定日期被标记为“热门”的视频ID集合。
    *   **使用位置:** `HotRank` (定时任务)

### 4. Sorted Set (有序集合 / ZSet)
Sorted Set 与 Set 类似，但它的每个成员都会关联一个分数 (score)，Redis 通过这个分数来为集合中的成员进行排序。它非常适用于排行榜、Feed流和历史记录等场景。

*   **键名格式:** `hot:rank`
    *   **用途:** 作为全局的热门视频排行榜。视频信息作为成员，根据计算出的热度值 (hot score) 进行排序。
    *   **使用位置:** `HotRank` (定时任务), `VideoServiceImpl`
*   **键名格式:** `user:history:video:{userId}`
    *   **用途:** 存储用户的视频浏览历史记录。视频对象作为成员，以浏览时间的时间戳作为分数进行排序。
    *   **使用位置:** `VideoServiceImpl`
*   **键名格式:** `out:follow:feed:{userId}`
    *   **用途:** 实现关注 Feed 流的“发件箱”(Outbox)模式。存储用户发布的视频ID，并以发布时间作为分数。
    *   **使用位置:** `FeedServiceImpl`
*   **键名格式:** `in:follow:feed:{userId}`
    *   **用途:** 实现关注 Feed 流的“收件箱”(Inbox)模式。存储用户所关注的人发布的视频ID，并以视频发布时间作为分数。
        ar **使用位置:** `FeedServiceImpl`, `VideoServiceImpl`
*   **键名格式:** `user:search:history:{userId}`
    *   **用途:** 存储用户的搜索历史记录。搜索关键词作为成员，以搜索时间的时间戳作为分数。
    *   **使用位置:** `UserServiceImpl`
*   **键名格式:** `user:follow:{userId}`
    *   **用途:** 存储用户的关注列表。被关注用户的ID作为成员，以关注时间的时间戳作为分数。
    *   **使用位置:** `FollowServiceImpl`
*   **键名格式:** `user:fans:{userId}`
    *   **用途:** 存储用户的粉丝列表。粉丝的用户ID作为成员，以关注时间的时间戳作为分数。
    *   **使用位置:** `FollowServiceImpl`