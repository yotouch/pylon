package com.yotouch.core.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yotouch.core.Consts;

public class WorkflowStateImpl implements WorkflowState {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowStateImpl.class);
    
    public static final WorkflowStateImpl ANY_STATE = new WorkflowStateImpl(Consts.WORKFLOW_STATE_ANY_STATE);
    public static final WorkflowStateImpl SELF_STATE = new WorkflowStateImpl(Consts.WORKFLOW_STATE_SELF_STATE);

    private WorkflowImpl wf;
    
    private String name;
    private String displayName;
    private String type;
    
    private Map<String, WorkflowAction> inActionMap;
    private Map<String, WorkflowAction> outActionMap;
    

    public WorkflowStateImpl(String name) {
        this.name = name;
        
        this.inActionMap = new HashMap<>();
        this.outActionMap = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getDisplayName() {
        return this.displayName;
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
    
    public void addOutAction(WorkflowActionImpl wfAction) {
        this.outActionMap.put(wfAction.getName(), wfAction);        
    }
    
    public void addInAction(WorkflowActionImpl wfAction) {
        this.inActionMap.put(wfAction.getName(), wfAction);        
    }
    
    @Override
    public List<WorkflowAction> getOutActions() {
        
        List<WorkflowAction> outActions = new ArrayList<>();
        
        for (WorkflowAction act: this.outActionMap.values()) {
            buildOutActionState(outActions, act);
        }
        
        for (WorkflowAction act: this.wf.getFromAnyActions()) {
            buildOutActionState(outActions, act);
        }

        return outActions;
    }

    private void buildOutActionState(List<WorkflowAction> outActions, WorkflowAction act) {
        WorkflowActionImpl wai = new WorkflowActionImpl(act.getName());
        wai.setWorkflow(this.wf);
        wai.setFrom(this);
        wai.setDisplayName(act.getDisplayName());
        wai.setType(act.getType());
        
        WorkflowStateImpl toState = (WorkflowStateImpl) act.getTo();
        if (toState.getName().equalsIgnoreCase(Consts.WORKFLOW_STATE_SELF_STATE)) {
            wai.setTo(this);
        } else {
            wai.setTo(toState);
        }
        
        outActions.add(wai);
    }

    @Override
    public List<WorkflowAction> getInActions() {
        List<WorkflowAction> list = new ArrayList<>(this.inActionMap.values());
        
        for (WorkflowAction act: this.outActionMap.values()) {
            buildInActionState(list, act);
        }
        
        List<WorkflowAction> fromAnyActions = this.wf.getFromAnyActions();
        logger.info("FromAnyActions " + fromAnyActions);
        for (WorkflowAction act: fromAnyActions) {
            buildInActionState(list, act);
        }        
        
        return list;
        
    }

    @Override
    public Workflow getWorkflow() {
        return this.wf;
    }

    private void buildInActionState(List<WorkflowAction> list, WorkflowAction act) {
        WorkflowActionImpl wai = new WorkflowActionImpl(act.getName());
        wai.setWorkflow(this.wf);
        wai.setFrom(this);
        wai.setDisplayName(act.getDisplayName());
        wai.setType(act.getType());
        
        WorkflowState toState = act.getTo();
        if (toState.equals(WorkflowStateImpl.SELF_STATE)) {
            wai.setTo(this);
            list.add(wai);
        } else {
            wai.setTo((WorkflowStateImpl) toState);
        }
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
        
        WorkflowStateImpl other = (WorkflowStateImpl) obj;
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

    @Override
    public String toString() {
        return "WorkflowStateImpl [wf=" + wf + ", name=" + name + "]";
    }

    

    
    
    

}
