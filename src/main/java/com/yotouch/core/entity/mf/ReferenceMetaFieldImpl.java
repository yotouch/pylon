package com.yotouch.core.entity.mf;

import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaFieldImpl;

public abstract class ReferenceMetaFieldImpl<T> extends MetaFieldImpl<T> {
    
    protected EntityManager entityMgr;
    
    String targetMetaEntityName;
    
    public ReferenceMetaFieldImpl(EntityManager entityMgr, String targetMetaEntityName) {
        this.entityMgr = entityMgr;
        this.targetMetaEntityName = targetMetaEntityName;
    }
    
    @Override
    public boolean isReference() {
        return true;
    }
    
    @Override
    public MetaEntity getTargetMetaEntity() {
        return this.entityMgr.getMetaEntity(this.targetMetaEntityName);
    }

}
