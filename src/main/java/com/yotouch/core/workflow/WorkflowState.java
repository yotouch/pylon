package com.yotouch.core.workflow;

import java.util.List;

public interface WorkflowState {

    String getName();
    
    String getDisplayName();

    boolean isStart();

    boolean isFinish();

    List<WorkflowAction> getOutActions();

    List<WorkflowAction> getInActions();

    Workflow getWorkflow();
}
