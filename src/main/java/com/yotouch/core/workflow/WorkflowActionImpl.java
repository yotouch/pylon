package com.yotouch.core.workflow;

public class WorkflowActionImpl implements WorkflowAction {
    
    private WorkflowImpl wf;
    
    private String name;
    
    private WorkflowStateImpl fromState;
    private WorkflowStateImpl toState;
    
    private String displayName;
    

    public WorkflowActionImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public WorkflowState getFrom() {
        return this.fromState;
    }

    @Override
    public WorkflowState getTo() {
        return this.toState;
    }

    public void setWorkflow(WorkflowImpl wfi) {
        this.wf = wfi;        
    }

    public WorkflowActionImpl setFrom(WorkflowStateImpl state) {
        this.fromState = state;
        return this;
    }

    public WorkflowActionImpl setTo(WorkflowStateImpl toState) {
        this.toState = toState;
        return this;        
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;        
    }

    @Override
    public String toString() {
        return "WorkflowActionImpl [wf=" + wf + ", name=" + name + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((wf == null) ? 0 : wf.hashCode());
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
        WorkflowActionImpl other = (WorkflowActionImpl) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (wf == null) {
            if (other.wf != null)
                return false;
        } else if (!wf.equals(other.wf))
            return false;
        return true;
    }
    
    
    

}
