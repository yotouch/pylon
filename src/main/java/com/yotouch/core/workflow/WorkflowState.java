package com.yotouch.core.workflow;

public interface WorkflowState {

    String getName();

    boolean isStart();

    boolean isFinish();

}
