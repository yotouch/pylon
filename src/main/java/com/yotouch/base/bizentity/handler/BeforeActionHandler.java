package com.yotouch.base.bizentity.handler;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowException;

import java.util.Map;

public interface BeforeActionHandler extends ActionHook {

    void doBeforeAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) throws WorkflowException;

    <M extends EntityModel> void doBeforeAction(WorkflowAction workflowAction, M entityModel, Map<String, Object> args) throws WorkflowException;
}
