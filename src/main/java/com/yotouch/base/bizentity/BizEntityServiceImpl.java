package com.yotouch.base.bizentity;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yotouch.base.bizentity.handler.AfterActionHandler;
import com.yotouch.base.bizentity.handler.BeforeActionHandler;
import com.yotouch.base.bizentity.handler.CanDoActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        
        BizEntity be = this.convert(bme.getWorkflow(), entity);
        
        return be;
    }

    @Override
    public BizEntity convert(Entity entity) {
        
        BizMetaEntity bme = this.beMgr.getBizMetaEntityByEntity(entity.getMetaEntity().getName());
        
        BizEntityImpl bei = new BizEntityImpl(bme, entity);
        
        return bei;
    }

    @Override
    public BizEntity convert(Workflow workflow, Entity entity) {

        BizMetaEntity bme = this.beMgr.getBizMetaEntityByWorkflow(workflow.getName());

        BizEntityImpl bei = new BizEntityImpl(bme, entity);

        return bei;
    }

    @Override
    public BizEntity doAction(DbSession dbSession, String actionName, BizEntity bizEntity) {
        return this.doAction(dbSession, actionName, bizEntity.getEntity());
    }

    @Override
    public BizEntity doAction(DbSession dbSession, String actionName, Entity entity) {

        WorkflowAction wfa = checkWorkflowAndGetAction(null, actionName, entity);
        
        entity.setValue(Consts.BIZ_ENTITY_FIELD_STATE, wfa.getTo().getName());
        entity = dbSession.save(entity);
        return this.convert(wfa.getWorkflow(), entity);
    }

    private WorkflowAction checkWorkflowAndGetAction(String workflowName, String actionName, Entity entity) {
        BizMetaEntity bme = null;
        if (StringUtils.isEmpty(workflowName)){
            bme = this.beMgr.getBizMetaEntityByEntity(entity.getMetaEntity().getName());
        } else {
            bme = this.beMgr.getBizMetaEntityByWorkflow(workflowName);
        }

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
        return wfa;
    }

    @Override
    public BizEntity doAction(DbSession dbSession, String actionName, Entity entity, BeforeActionHandler beforeActionHandler, AfterActionHandler afterActionHandler, Map<String, Object> args) throws WorkflowException {

        TransitResult tr = doTransit(dbSession, null, actionName, entity, beforeActionHandler, args);
        entity = tr.entity;
        WorkflowAction wfa = tr.wfa;
        
        afterActionHandler.doAfterAction(dbSession, wfa, entity, args);

        return this.convert(wfa.getWorkflow(), entity);
    }

    @Override
    public BizEntity doAction(DbSession dbSession, String workflowName, String actionName, Entity entity, BeforeActionHandler beforeActionHandler, AfterActionHandler afterActionHandler, Map<String, Object> args) throws WorkflowException {
        TransitResult tr = doTransit(dbSession, workflowName, actionName, entity, beforeActionHandler, args);
        entity = tr.entity;
        WorkflowAction wfa = tr.wfa;

        afterActionHandler.doAfterAction(dbSession, wfa, entity, args);

        return this.convert(wfa.getWorkflow(), entity);
    }

    @Override
    public boolean canDoAction(DbSession dbSession, WorkflowAction wa, Entity entity, CanDoActionHandler canDoActionHandler, Map<String, Object> args) {
        return canDoActionHandler.canDoAction(dbSession, wa, entity, args);
    }

    @Transactional
    private TransitResult doTransit(DbSession dbSession, String workflowName, String actionName, Entity entity, BeforeActionHandler beforeActionHandler, Map<String, Object> args) {
        WorkflowAction wfa = checkWorkflowAndGetAction(workflowName, actionName, entity);
        beforeActionHandler.doBeforeAction(dbSession, wfa, entity, args);

        entity.setValue(Consts.BIZ_ENTITY_FIELD_STATE, wfa.getTo().getName());
        List<Map<String, Object>> diffList = entity.diffValueList();
        entity = dbSession.save(entity);

        Entity wfaLog = dbSession.newEntity("workflowActionLog");
        wfaLog.setValue("action", actionName);
        wfaLog.setValue("workflow", wfa.getWorkflow().getName());
        wfaLog.setValue("entityUuid", entity.getUuid());
        wfaLog.setValue("entityName", entity.getMetaEntity().getName());

        Map<String, Object> actionLog = (Map<String, Object>) args.get("actionLog");
        if (actionLog != null) {
            wfaLog.setValue("note", actionLog.get(Consts.WORKFLOW_ACTION_LOG_NOTE));
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            String s = mapper.writeValueAsString(diffList);
            wfaLog.setValue("extra", s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        dbSession.save(wfaLog);

        return new TransitResult(wfa, entity);
    }

    class TransitResult {
        WorkflowAction wfa;
        Entity entity;

        public TransitResult(WorkflowAction wfa, Entity entity) {
            this.wfa = wfa;
            this.entity = entity;
        }
    }


}
