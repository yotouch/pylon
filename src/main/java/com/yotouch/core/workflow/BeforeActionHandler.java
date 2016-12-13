package com.yotouch.core.workflow;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

public interface BeforeActionHandler extends ActionHook {

    void doBeforeAction(DbSession dbSession, String actionName, Entity entity) throws WorkflowException;

}
