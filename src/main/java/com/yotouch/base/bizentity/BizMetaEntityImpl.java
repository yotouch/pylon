package com.yotouch.base.bizentity;

import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.workflow.Workflow;

public class BizMetaEntityImpl implements BizMetaEntity {
    
    private Workflow wf;
    private MetaEntity me;

    public BizMetaEntityImpl(Workflow wf, MetaEntity me) {
        this.wf = wf;
        this.me = me;
    }

    @Override
    public Workflow getWorkflow() {
        return wf;
    }

    @Override
    public MetaEntity getMetaEntity() {
        return me;
    }

}
