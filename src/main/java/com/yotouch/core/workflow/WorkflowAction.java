package com.yotouch.core.workflow;

public interface WorkflowAction {

    Workflow getWorkflow();

    String getName();

    WorkflowState getFrom();

    WorkflowState getTo();
    
    String getType();

    String getDisplayName();

}
