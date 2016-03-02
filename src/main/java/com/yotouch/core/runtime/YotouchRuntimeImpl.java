package com.yotouch.core.runtime;

import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.store.db.DbStore;


public class YotouchRuntimeImpl implements YotouchRuntime {
    
    private EntityManager entityMgr;
    private DbStore dbStore;
    
    public YotouchRuntimeImpl(EntityManager entityMgr, DbStore dbStore) {
        this.entityMgr = entityMgr;
        this.dbStore = dbStore;
    }

    @Override
    public DbSession createDbSession() {
        return new DbSessionImpl(this.entityMgr, this.dbStore);
    }


}
