package com.yotouch.core.workflow;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

public interface AfterActionHandler extends ActionHook {

    default void doAfterAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity) {

    }

}
