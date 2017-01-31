package com.yotouch.base.workflow;

import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.StateFormHandler;
import com.yotouch.core.workflow.WorkflowState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScriptingStateFormHandler implements StateFormHandler {

    @Autowired
    private ScriptActionHandlerHelper saHelper;
    
    @Override
    public List<String> getFieldList(DbSession dbSession, WorkflowState workflowState) {
        return (List<String>) saHelper.doEvalStateScript(dbSession, workflowState, "fieldList");
    }
    
    
}
