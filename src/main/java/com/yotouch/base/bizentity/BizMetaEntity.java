package com.yotouch.base.bizentity;

import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowException;

public interface BizMetaEntity extends MetaEntity {

    Workflow getWorkflow();

    BizEntity doAction(DbSession dbSession) throws WorkflowException;

    BizEntity startWorkflow(DbSession dbSession, WorkflowAction action);

    BizEntity prepareWorkflow();
}
