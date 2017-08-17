package com.yotouch.base.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

    @Value("${cache.name:}")
    String cacheName;

    @Autowired(required = false)
    private RedisTemplate<String, String> template;
    
    // inject the template as ListOperations

    @PostConstruct
    public void init() {
        
    }

    @Override
    public void set(String key, String value) {
        template.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
        return template.opsForValue().get(key);
    }

    @Override
    public void del(String key) {
        template.delete(key);
    }
}
