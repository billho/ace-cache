package com.ace.cache.service.impl;

import com.ace.cache.service.IBaseCacheService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Service
public class RedisAndLocalCacheServiceImpl extends RedisServiceImpl implements IBaseCacheService {
    private static final String CacheName= "smLocalCache";
//    @Autowired
//    private RedisConfig redisConfig;
//    @Autowired
//    public void init(){
//        /**
//         * 超时设置为redis的一半，以激活redis
//         */
//        //localCache = CacheUtil.newLRUCache(redisConfig.getLocalCacheSize().intValue(),DateUnit.MINUTE.getMillis()* 60);
//
//    }

    @Override
    public String get(String key) {
        if (isEnable()){
            Map<String,String> cache = localCache();
            String val = cache.containsKey(key)? cache.get(key):
                    super.get(key);
            return val;
        }
        else{
            return  super.get(key);
        }

    }

    @Override
    public Set<String> getByPre(String pre) {
        if (isEnable()) {
            Set<String> keys = getLocalCacheByPre(pre);
            if (null == keys) {
                return super.getByPre(pre);
            } else {
                Set<String> superKeys = super.getByPre(pre);
                if (null != superKeys&& ! superKeys.isEmpty()) {
                    keys.addAll(superKeys);
                }
                return keys;
            }
        }
        else {
            return super.getByPre(pre);
        }
    }

    @Override
    public String set(String key, String value) {
        if (isEnable()) {
            this.localCache().put(key, value);
            return super.set(key, value);
        }
        else {
            return super.set(key,value);
        }
    }

    @Override
    public String set(String key, String value, int expire) {
        if (isEnable()){
            this.localCache().put(key,value);
            return super.set(key,value,expire);
        }
        else {
            return super.set(key,value,expire);
        }
    }

    @Override
    public Long delPre(String key) {
        if (isEnable()){
            Set<String> keys = getLocalCacheByPre(key);
            Map<String,String> map = localCache();
            for (String k :
                    keys) {
                map.remove(k);
            }
            return super.delPre(key);

        }
        else
        {
            return super.delPre(key);
        }
    }

    @Override
    public Long del(String... keys) {
       if (isEnable()){
           if (keys.length>0){
               Map<String,String> map = this.localCache();
               if (!map.isEmpty()) {
                   for (String key :
                           keys) {
                       if (map.containsKey(key)){
                           map.remove(key);
                       }
                   }
               }
               return super.del(keys);
           }
           return 0L;
       }
       else {
           return super.del(keys);
       }
    }

//    @Override
//    public Date getExpireDate(String key) {
//        return null;
//    }

    private Set<String> getLocalCacheByPre(String pre){
        Iterator<Map.Entry<String,String>> iterator = localCache().entrySet().iterator();
        Set<String> set = Sets.newHashSet();
        while (iterator.hasNext()){
            String key = iterator.next().getKey();
            if (StringUtils.startsWith(key,pre)){
                set.add(key);
            }
        }
        return set;
    }

    private boolean isEnable(){
        ServletRequestAttributes servletContainer = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletContainer){
            return false;
        }
        else {
            return true;
        }
    }
    private Map<String,String> localCache(){
        ServletRequestAttributes servletContainer = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletContainer.getRequest();
        Map<String,String> map =  (Map<String,String>) request.getAttribute(CacheName);
        if (null == map) {
            map = new WeakHashMap<>();
            request.setAttribute(CacheName,map);
        }
        return map;
    }

}
