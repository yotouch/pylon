package com.yotouch.core.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.yotouch.core.Consts;
import com.yotouch.core.config.Configure;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.store.db.DbStore;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = "singleton")
public class YotouchApplicationImpl implements YotouchApplication {

    private static final Logger logger = LoggerFactory.getLogger(YotouchApplicationImpl.class);
    
    @Autowired
    private EntityManager entityMgr;
        
    @Autowired
    private DbStore dbStore;

    private Map<String, Object> attrs ;

    @PostConstruct
    void init() {
        this.attrs = new HashMap<>();
        logger.info("=========================================" + System.identityHashCode(this.attrs));
    }

    @Override
    public YotouchRuntime getRuntime() {
        return new YotouchRuntimeImpl(this.entityMgr, this.dbStore);
    }

    @Override
    public EntityManager getEntityManager() {
        return this.entityMgr;
    }

    @Override
    public void setAttribute(String key, Object value) {

        logger.info("Set attr " + key + " value " + value + " => " + System.identityHashCode(this.attrs));
        this.attrs.put(key, value);

        this.getAttribute(key);
    }

    @Override
    public Object getAttribute(String key) {
        logger.info("Get attr " + key + " value " + this.attrs.get(key) + " => " + System.identityHashCode(this.attrs));

        return this.attrs.get(key);
    }

}
