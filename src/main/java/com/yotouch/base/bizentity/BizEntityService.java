package com.yotouch.base.bizentity;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

public interface BizEntityService {
    
    BizEntity prepareWorkflow(BizMetaEntity bme);

    BizEntity convert(Entity entity);

    BizEntity doAction(DbSession dbSession, String actionName, Entity entity);

}

