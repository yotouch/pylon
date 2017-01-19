package com.yotouch.base.workflow;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.AfterActionHandler;
import com.yotouch.core.workflow.WorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScriptingAfterActionHandler implements AfterActionHandler {

    @Autowired
    private ScriptActionHandlerHelper saHelper;

    @Override
    public void doAfterAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) {
        saHelper.doEvalScript(dbSession, workflowAction, entity, "after", args);
    }

}
