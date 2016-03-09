package com.yotouch.core.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowImpl implements Workflow {
    
    private String name;
    private Map<String, WorkflowState> stateMap;
    
    private WorkflowState startState;
    private List<WorkflowState> finishStates;

    public WorkflowImpl(String name) {
        this.name = name;
        this.stateMap = new HashMap<>();
        this.finishStates = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public WorkflowImpl addState(WorkflowState state) {
        this.stateMap.put(state.getName(), state);
        if (state.isStart()) {
            this.startState = state;
        } else if (state.isFinish()) {
            this.finishStates.add(state);
        }
        
        return this;
    }

    @Override
    public WorkflowState getStartState() {
        return this.startState;
    }

    @Override
    public List<WorkflowState> getFinishStates() {
        return new ArrayList<>(this.finishStates);
    }

}
