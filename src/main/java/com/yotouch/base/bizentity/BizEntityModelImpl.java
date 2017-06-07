package com.yotouch.base.bizentity;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.util.EntityUtil;
import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowState;
import org.springframework.util.StringUtils;

/**
 * Created by king on 6/6/17.
 */
public class BizEntityModelImpl implements BizEntity {
    private Workflow    workflow;
    private EntityModel entityModel;

    public <M extends EntityModel> BizEntityModelImpl(Workflow workflow, M entityModel) {
        this.workflow = workflow;
        this.entityModel = entityModel;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    @Override
    public WorkflowState getState() {
        String wf_state = this.entityModel.getWfState();

        if (StringUtils.isEmpty(wf_state)) {
            return null;
        }

        return this.workflow.getState(wf_state);
    }

    @Override
    public Entity getEntity() {
        return EntityUtil.convert(entityModel);
    }

    public <M extends EntityModel> M getEntityModel() {
        return (M) entityModel;
    }
}
