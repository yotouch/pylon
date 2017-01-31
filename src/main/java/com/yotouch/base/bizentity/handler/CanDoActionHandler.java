package com.yotouch.base.bizentity.handler;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.WorkflowAction;

import java.util.Map;

public interface CanDoActionHandler extends ActionHook {

    boolean canDoAction(DbSession dbSession, WorkflowAction wfa, Entity entity, Map<String, Object> args);

}
