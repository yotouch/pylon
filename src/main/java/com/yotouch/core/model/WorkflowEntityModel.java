package com.yotouch.core.model;

import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowState;
import org.springframework.stereotype.Component;

/**
 * Created by king on 6/7/17.
 */
@Component
public class WorkflowEntityModel<M extends EntityModel> {
    private Workflow workflow;
    private M entityModel;

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public M getEntityModel() {
        return entityModel;
    }

    public void setEntityModel(M entityModel) {
        this.entityModel = entityModel;
    }
}
