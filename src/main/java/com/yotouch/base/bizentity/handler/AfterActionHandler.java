package com.yotouch.base.bizentity.handler;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.WorkflowAction;

import java.util.Map;

public interface AfterActionHandler extends ActionHook {

    void doAfterAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args);

    <M extends EntityModel> void doAfterAction(WorkflowAction workflowAction, M entityModel, Map<String, Object> args);
}
