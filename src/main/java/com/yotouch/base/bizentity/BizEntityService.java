package com.yotouch.base.bizentity;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.WorkflowException;

public interface BizEntityService {
    
    BizEntity prepareWorkflow(BizMetaEntity bme);

    BizEntity doAction(DbSession dbSession) throws WorkflowException;
    
    BizEntity convert(Entity entity);

}

