package com.yotouch.base.bizentity;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.AfterActionHandler;
import com.yotouch.core.workflow.BeforeActionHandler;
import com.yotouch.core.workflow.CanDoActionHandler;
import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowException;

import java.util.Map;

public interface BizEntityService {
    
    BizEntity prepareWorkflow(BizMetaEntity bme);

    BizEntity convert(Entity entity);

    BizEntity doAction(DbSession dbSession, String actionName, BizEntity bizEntity);

    @Deprecated
    BizEntity doAction(DbSession dbSession, String actionName, Entity entity);

    BizEntity doAction(DbSession dbSession, String actionName, Entity entity, BeforeActionHandler beforeActionHandler, AfterActionHandler afterActionHandler, Map<String, Object> args) throws WorkflowException;

    boolean canDoAction(DbSession dbSession, WorkflowAction wa, Entity entity, CanDoActionHandler canDoActionHandler, Map<String, Object> args);
}

