package com.yotouch.core.workflow;

import java.util.List;

public interface Workflow {

    String getName();

    WorkflowState getStartState();

    List<WorkflowState> getFinishStates();

    WorkflowState getState(String name);

    WorkflowAction getAction(String name);

}
