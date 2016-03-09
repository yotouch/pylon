package com.yotouch.core.workflow;

public class WorkflowStateImpl implements WorkflowState {
    
    private Workflow wf;
    
    private String name;
    private String displayName;
    private String type;

    public WorkflowStateImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setWorkflow(WorkflowImpl wfi) {
        this.wf = wfi;        
    }

    public WorkflowStateImpl setDisplayName(String stateDispName) {
        this.displayName = stateDispName;
        return this;        
    }

    public WorkflowStateImpl setType(String type) {
        this.type = type;
        return this;        
    }

}
