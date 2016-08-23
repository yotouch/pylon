package com.yotouch.base.cache;

import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.runtime.DbSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;

import java.lang.reflect.Method;

public class EntityKeyGenerator implements KeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(EntityKeyGenerator.class);

    @Override
    public Object generate(Object target, Method method, Object... params) {

        logger.info("Gen entity key " + method.getName() + " params " + params[1]);

        if (target instanceof DbSession) {

            if (method.getName().equals("getEntity")) {

                if (params.length > 1) {

                    MetaEntity me = (MetaEntity) params[0];
                    String uuid = (String) params[1];

                    return "Entity:" + me.getName() + ":" + uuid;
                }
            }
        }

        return SimpleKey.EMPTY;


    }
}
