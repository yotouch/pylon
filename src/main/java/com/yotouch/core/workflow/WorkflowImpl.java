package com.yotouch.core.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowImpl implements Workflow {
    
    private String uuid;
    private String name;
    private String displayName;

    private Map<String, WorkflowState> stateMap;
    private WorkflowState startState;
    private List<WorkflowState> finishStates;
    
    private Map<String, WorkflowAction> actionMap;    
    private Map<String, WorkflowAction> fromAnyActionMap;
    

    public WorkflowImpl(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.stateMap = new HashMap<>();
        this.finishStates = new ArrayList<>();
        this.actionMap = new HashMap<>();
        this.fromAnyActionMap = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getUuid() {
        return this.uuid;
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

    @Override
    public WorkflowState getState(String name) {
        return this.stateMap.get(name);
    }

    @Override
    public WorkflowAction getAction(String name) {
        return this.actionMap.get(name);
    }

    public void addAction(WorkflowActionImpl wfAction) {
        this.actionMap.put(wfAction.getName(), wfAction);
        
        if (wfAction.getFrom().equals(WorkflowStateImpl.ANY_STATE)) {
            this.fromAnyActionMap.put(wfAction.getName(), wfAction);
        }
    }
    
    public List<WorkflowAction> getFromAnyActions() {
        return new ArrayList<>(this.fromAnyActionMap.values());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkflowImpl other = (WorkflowImpl) obj;
        if (uuid == null) {
            if (other.uuid!= null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorkflowImpl [name=" + name + "]";
    }

    
    

}
