package com.ace.cache.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.ace.cache.config.RedisConfig;
import com.ace.cache.service.IBaseCacheService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

@Service
public class RedisAndLocalCacheServiceImpl extends RedisServiceImpl implements IBaseCacheService {

    Cache<String,String> localCache;
    @Autowired
    private RedisConfig redisConfig;
    @Autowired
    public void init(){
        /**
         * 超时设置为redis的一半，以激活redis
         */
        localCache = CacheUtil.newLRUCache(redisConfig.getLocalCacheSize().intValue(),DateUnit.MINUTE.getMillis()* 60);
    }

    @Override
    public String get(String key) {
        String val = localCache.containsKey(key)? localCache.get(key):
                super.get(key);
        return val;
    }

    @Override
    public Set<String> getByPre(String pre) {
        Set<String> keys = getLocalCacheByPre(pre);
        Set<String> superKeys = super.getByPre(pre);
        if (null!= superKeys){
            keys.addAll(superKeys);
        }
        return keys;
    }

    @Override
    public String set(String key, String value) {
        this.localCache.put(key,value);
        return super.set(key,value);
    }

    @Override
    public String set(String key, String value, int expire) {
        this.localCache.put(key,value,expire * DateUnit.MINUTE.getMillis()/12);
        return super.set(key,value,expire);
    }

    @Override
    public Long delPre(String key) {
        Set<String> keys = getLocalCacheByPre(key);
        for (String k :
                keys) {
            this.localCache.remove(k);
        }
        return super.delPre(key);
    }

    @Override
    public Long del(String... keys) {
        if (keys.length>0){
            for (String key :
                    keys) {
                if (this.localCache.containsKey(key)) {
                    this.localCache.remove(key);
                }
            }
            return super.del(keys);
        }
        return 0L;
    }

//    @Override
//    public Date getExpireDate(String key) {
//        return null;
//    }

    private Set<String> getLocalCacheByPre(String pre){
        Iterator<CacheObj<String,String>> iterator = localCache.cacheObjIterator();
        Set<String> set = Sets.newHashSet();
        while (iterator.hasNext()){
            String key = iterator.next().getKey();
            if (StringUtils.startsWith(key,pre)){
                set.add(key);
            }
        }
        return set;
    }
}
