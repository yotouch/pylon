package com.yotouch.base.workflow;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.CanDoActionHandler;
import com.yotouch.core.workflow.WorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScriptingCanDoActionHandler implements CanDoActionHandler {

    @Autowired
    private ScriptActionHandlerHelper saHelper;
    
    @Override
    public boolean canDoAction(DbSession dbSession, WorkflowAction wfa, Entity entity, Map<String, Object> args) {
        Boolean canDo = (Boolean) saHelper.doEvalScript(dbSession, wfa, entity, "canDo", args);
        if (canDo == null) {
            return false;
        }
        return canDo;
    }
    
    
}
