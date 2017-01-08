package com.yotouch.base.bizentity;

public interface BizEntityManager {

    void reload();
    
    
    /**
     * 现在 Workflow 和 MetaEntity 是一一对应的关系
     * 
     * @param wfName
     * @return
     */
    BizMetaEntity getBizMetaEntityByEntity(String entityName);

}
