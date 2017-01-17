package com.yotouch.base.bizentity;

public interface BizEntityManager {

    void reload();
    
    
    /**
     * 现在 Workflow 和 MetaEntity 是一一对应的关系
     * 
     * 应该是一对多的关系，所以 BizMetaEntity 这个概念可能有问题
     * 
     * @param wfName
     * @return
     */
    @Deprecated
    BizMetaEntity getBizMetaEntityByEntity(String entityName);
    
    BizMetaEntity getBizMetaEntityByWorkflow(String wfName);

}
