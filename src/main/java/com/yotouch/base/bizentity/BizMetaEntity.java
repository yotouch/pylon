package com.yotouch.base.bizentity;

import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.workflow.Workflow;

public interface BizMetaEntity {

    Workflow getWorkflow();

    MetaEntity getMetaEntity();

}
