package com.yotouch.core.workflow;

import com.yotouch.core.runtime.DbSession;

import java.util.List;

public interface StateFormHandler {
    
    List<String> getFieldList(DbSession dbSession, WorkflowState workflowState);
    
}
