package com.yotouch.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Service
public class RedisServiceImpl implements RedisService {

    @Value("${cache.name:}")
    String cacheName;

    @Autowired
    private StringRedisTemplate template;

    private RedisCache cache;

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(cacheName)) {
            cacheName = "";
        }

        if (!cacheName.endsWith(":")) {
            cacheName = cacheName + ":";
        }
        cache = new RedisCache(cacheName, cacheName.getBytes(), template, 0);
    }

    @Override
    public void set(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public String get(String key) {
        Cache.ValueWrapper v = cache.get(key);
        if (v != null) {
            return (String) v.get();
        }

        return null;
    }

    @Override
    public void del(String key) {
        cache.evict(key);
    }
}
