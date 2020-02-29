package com.ace.cache.service;

import java.util.Date;
import java.util.Set;

/**
 * 基本的缓存操作
 */
public interface IBaseCacheService {
    /**
     * <p>
     * 通过key获取储存在redis中的value
     * </p>
     * <p>
     * 并释放连接
     * </p>
     *
     * @param key
     * @return 成功返回value 失败返回null
     */
    String get(String key);

    /**
     * <p>
     * 通过前缀获取储存在redis中的value
     * </p>
     * <p>
     * 并释放连接
     * </p>
     *
     * @param pre
     * @return 成功返回value 失败返回null
     */
    Set<String> getByPre(String pre);

    /**
     * <p>
     * 向redis存入key和value,并释放连接资源
     * </p>
     * <p>
     * 如果key已经存在 则覆盖
     * </p>
     *
     * @param key
     * @param value
     * @return 成功 返回OK 失败返回 0
     */
    String set(String key, String value);

    /**
     * <p>
     * 向redis存入key和value,并释放连接资源
     * </p>
     * <p>
     * 如果key已经存在 则覆盖
     * </p>
     *
     * @param key
     * @param value
     * @return 成功 返回OK 失败返回 0
     */
    String set(String key, String value, int expire);

    /**
     * <p>
     * 根据前缀移除key
     * </p>
     *
     * @param key 一个key 也可以使 string 数组
     * @return 返回删除成功的个数
     */
    Long delPre(String key);

    /**
     * <p>
     * 删除指定的key,也可以传入一个包含key的数组
     * </p>
     *
     * @param keys 一个key 也可以使 string 数组
     * @return 返回删除成功的个数
     */
    Long del(String... keys);

    /**
     * <p>
     * 通过key判断值得类型
     *
     * </p>
     *
     * @param key
     * @return
     */
    String type(String key);

    /**
     * 获取key过期时间
     *
     * @param key
     * @return
     */
    Date getExpireDate(String key);
    ///////////////////////////////////

}
