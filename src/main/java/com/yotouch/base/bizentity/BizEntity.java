package com.yotouch.base.bizentity;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowState;

public interface BizEntity  {

    Workflow getWorkflow();

    WorkflowState getState();
    
    Entity getEntity();

}
