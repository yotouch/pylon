package com.yotouch.core.workflow;

import java.util.HashMap;
import java.util.Map;

public class WorkflowImpl implements Workflow {
    
    private String name;
    private Map<String, WorkflowState> stateMap;

    public WorkflowImpl(String name) {
        this.name = name;
        this.stateMap = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public WorkflowImpl addState(WorkflowState state) {
        this.stateMap.put(state.getName(), state);
        return this;
    }

}
