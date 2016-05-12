package com.yotouch.core.runtime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yotouch.core.Consts;
import com.yotouch.core.config.Configure;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.store.db.DbStore;
import org.springframework.util.StringUtils;

@Service
public class YotouchApplicationImpl implements YotouchApplication {
    
    @Autowired
    private EntityManager entityMgr;
        
    @Autowired
    private DbStore dbStore;
    
    @Override
    public YotouchRuntime getRuntime() {
        return new YotouchRuntimeImpl(this.entityMgr, this.dbStore);
    }

    @Override
    public EntityManager getEntityManager() {
        return this.entityMgr;
    }

}
