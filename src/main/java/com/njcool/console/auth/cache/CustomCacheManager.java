package com.njcool.console.auth.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc 权限缓存管理器 可以适配 默认使用redis做缓存载体
 *
 */
@Component
public class CustomCacheManager<K, V> implements CacheManager {

    @Autowired
    private RedisTemplate redisTemplate;

    private final Map<String, Cache<K, V>> cacheMap = new HashMap<String, Cache<K, V>>();

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
        /*if (cacheMap.containsKey(cacheName)) {
            throw new RuntimeException("Cache create duplicated! cacheKey: " + cacheName);
        }
        Cache cache = new RedisCache<V>(redisTemplate,cacheName, -1);
        cacheMap.put(cacheName, cache);
        return cache;*/
        return null;
    }
}
