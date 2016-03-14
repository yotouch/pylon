package com.yotouch.base.bizentity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowException;
import com.yotouch.core.workflow.WorkflowState;

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
    public BizEntity convert(Entity entity) {
        
        BizMetaEntity bme = this.beMgr.getBizMetaEntityByEntity(entity.getMetaEntity().getName());
        
        BizEntityImpl bei = new BizEntityImpl(bme, entity);
        
        return bei;
    }

    @Override
    public BizEntity doAction(DbSession dbSession, String actionName, Entity entity) {
        
        BizMetaEntity bme = this.beMgr.getBizMetaEntityByEntity(entity.getMetaEntity().getName());
        
        if (bme == null) {
            throw new WorkflowException("No such workflow for MetaEntity [ "+entity.getMetaEntity().getName()+"]");
        }
        
        Workflow wf = bme.getWorkflow();
        
        String stateName = entity.v(Consts.BIZ_ENTITY_FIELD_STATE);
        WorkflowState wfState = null;
        if (StringUtils.isEmpty(stateName)) {
            wfState = wf.getStartState();
        } else {
            wfState = wf.getState(stateName);
        }
        
        if (wfState == null) {
            throw new WorkflowException("No such state [" + entity.v(Consts.BIZ_ENTITY_FIELD_STATE) + "] for workfow [ " + wf.getName() + "]");
        }
        
        List<WorkflowAction> actList = wfState.getOutActions();
        WorkflowAction wfa = null;
        
        for (WorkflowAction act: actList) {
            if (act.getName().equalsIgnoreCase(actionName)) {
                wfa = act;
                break;
            }
        }                
        
        if (wfa == null) {
            throw new WorkflowException("No such action [" + actionName + "] for workflow [" + wf.getName() + "]");
        }
        
        // before action
        
        // transit
        entity.setValue(Consts.BIZ_ENTITY_FIELD_STATE, wfa.getTo().getName());
        entity = dbSession.save(entity);
        
        // after action
        
        return this.convert(entity);
    }
    
    

}
