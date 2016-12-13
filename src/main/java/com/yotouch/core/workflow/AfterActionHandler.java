package com.yotouch.core.workflow;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

public interface AfterActionHandler extends ActionHook {

    void doAfterAction(DbSession dbSession, String actionName, Entity entity);

}