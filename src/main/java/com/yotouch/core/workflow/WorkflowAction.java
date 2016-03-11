package com.yotouch.core.workflow;

public interface WorkflowAction {

    String getName();

    WorkflowState getFrom();

    WorkflowState getTo();

    String getDisplayName();

}
