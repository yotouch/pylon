package com.yotouch.base.bizentity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.WorkflowException;

@Service
public class BizEntityServiceImpl implements BizEntityService {
    
    @Autowired
    private BizEntityManager beMgr;

    @Override
    public BizEntity prepareWorkflow(BizMetaEntity bme) {
        
        MetaEntity me = bme.getMetaEntity();
        Entity entity = me.newEntity();
        entity.setValue(Consts.BIZ_ENTITY_FIELD_WORKFLOW, bme.getWorkflow().getName());
        entity.setValue(Consts.BIZ_ENTITY_FIELD_STATE, "");
        
        BizEntity be = this.convert(entity);
        
        return be;
    }

    @Override
    public BizEntity doAction(DbSession dbSession) throws WorkflowException {
        
        
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BizEntity convert(Entity entity) {
        
        BizMetaEntity bme = this.beMgr.getBizMetaEntityByEntity(entity.getMetaEntity().getName());
        
        BizEntityImpl bei = new BizEntityImpl(bme, entity);
        
        return bei;
    }

}
