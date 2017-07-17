package com.yotouch.base.bizentity;

import java.util.Map;

import com.yotouch.base.bizentity.handler.AfterActionHandler;
import com.yotouch.base.bizentity.handler.BeforeActionHandler;
import com.yotouch.base.bizentity.handler.CanDoActionHandler;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowException;


public interface BizEntityService {
    
    BizEntity prepareWorkflow(BizMetaEntity bme);

    @Deprecated
    BizEntity convert(Entity entity);

    BizEntity convert(Workflow workflow, Entity entity);

    <M extends EntityModel> M convert(Workflow workflow, M entityModel);

    @Deprecated
    BizEntity doAction(DbSession dbSession, String actionName, BizEntity bizEntity);

    @Deprecated
    BizEntity doAction(DbSession dbSession, String actionName, Entity entity);

    @Deprecated
    BizEntity doAction(DbSession dbSession, String actionName, Entity entity, BeforeActionHandler beforeActionHandler, AfterActionHandler afterActionHandler, Map<String, Object> args) throws WorkflowException;

    BizEntity doAction(DbSession dbSession, String workflowName, String actionName, Entity entity, BeforeActionHandler beforeActionHandler, AfterActionHandler afterActionHandler, Map<String, Object> args) throws WorkflowException;

    <M extends EntityModel> M doAction(DbSession dbSession, M entityModel, String actionName, BeforeActionHandler beforeActionHandler, AfterActionHandler afterActionHandler, Map<String, Object> args) throws WorkflowException;

    boolean canDoAction(DbSession dbSession, WorkflowAction wa, Entity entity, CanDoActionHandler canDoActionHandler, Map<String, Object> args);
}

