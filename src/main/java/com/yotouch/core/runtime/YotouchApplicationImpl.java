package com.yotouch.core.runtime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.yotouch.core.Consts;
import com.yotouch.core.config.Configure;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.store.db.DbStore;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = "singleton")
public class YotouchApplicationImpl implements YotouchApplication {
    
    @Autowired
    private EntityManager entityMgr;
        
    @Autowired
    private DbStore dbStore;

    @Autowired
    private Map<String, Object> attrs = new HashMap<>();

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
        this.attrs.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return this.attrs.get(key);
    }

}
