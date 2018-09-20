package com.njcool.console.auth.cache;

import com.njcool.console.common.utils.TimeUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author xfe
 * @Date 2018/9/16
 * @Desc
 */
public class RedisCache<V> implements Cache<String, V> {

    private String cacheName;

    private long expireTime = 3600;// 缓存的超时时间，单位为s

    private RedisTemplate<String, V> redisTemplate;// 通过构造方法注入该对象

    public RedisCache() {
        super();
    }

    public RedisCache(RedisTemplate<String, V> redisTemplate, String cacheName, long expireTime) {
        super();
        this.cacheName = StringUtils.isEmpty(cacheName)? "" : cacheName + "-";
        this.expireTime = expireTime > 0 ? expireTime : TimeUtils.getMillSecOnMoment(23, 59, 59, 999);
        this.redisTemplate = redisTemplate;
    }

    /**
     * 通过key来获取对应的缓存对象
     * 通过源码我们可以发现，shiro需要的key的类型为Object，V的类型为AuthorizationInfo对象
     */
    @Override
    public V get(String key) throws CacheException {
        return redisTemplate.opsForValue().get(cacheName + key);
    }

    /**
     * 将权限信息加入缓存中
     */
    @Override
    public V put(String key, V value) throws CacheException {
        redisTemplate.opsForValue().set(cacheName + key, value, this.expireTime, TimeUnit.SECONDS);
        return value;
    }

    /**
     * 将权限信息从缓存中删除
     */
    @Override
    public V remove(String key) throws CacheException {
        V v = redisTemplate.opsForValue().get(cacheName + key);
        redisTemplate.opsForValue().getOperations().delete(cacheName + key);
        return v;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<String> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;

    }
}
