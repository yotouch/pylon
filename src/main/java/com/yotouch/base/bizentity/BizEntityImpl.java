package com.yotouch.base.bizentity;

import org.springframework.util.StringUtils;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowState;

public class BizEntityImpl implements BizEntity {

    private BizMetaEntity bme;
    private Entity entity;

    public BizEntityImpl(BizMetaEntity bme, Entity entity) {
        this.bme = bme;
        this.entity = entity;
    }

    @Override
    public Workflow getWorkflow() {
        return this.bme.getWorkflow();
    }

    @Override
    public WorkflowState getState() {
        String stateName = this.entity.v(Consts.BIZ_ENTITY_FIELD_STATE);
        if (StringUtils.isEmpty(stateName)) {
            return null;
        }
        return this.getWorkflow().getState(stateName);
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }
}
