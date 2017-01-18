package com.yotouch.base.workflow;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.BeforeActionHandler;
import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScriptingBeforeActionHandler implements BeforeActionHandler {

    @Autowired
    private ScriptActionHandlerHelper saHelper;

    @Override
    public void doBeforeAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) throws WorkflowException {
        saHelper.doEvalScript(dbSession, workflowAction, entity, "before", args);
    }
}
