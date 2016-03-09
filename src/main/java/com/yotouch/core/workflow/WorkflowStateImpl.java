package com.yotouch.core.workflow;

import com.yotouch.core.Consts;

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

    @Override
    public boolean isStart() {
        return Consts.WORKFLOW_STATE_TYPE_START.equalsIgnoreCase(this.type);
    }

    @Override
    public boolean isFinish() {
        return Consts.WORKFLOW_STATE_TYPE_FINISH.equalsIgnoreCase(this.type);
    }

}
