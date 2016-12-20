package com.yotouch.core.workflow;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

public interface BeforeActionHandler extends ActionHook {

    default void doBeforeAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity) throws WorkflowException {

    }

}
