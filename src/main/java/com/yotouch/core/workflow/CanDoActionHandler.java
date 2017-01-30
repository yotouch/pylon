package com.yotouch.core.workflow;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

import java.util.Map;

public interface CanDoActionHandler extends ActionHook {

    boolean canDoAction(DbSession dbSession, WorkflowAction wfa, Entity entity, Map<String, Object> args);

}
