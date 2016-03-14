package com.yotouch.base.bizentity;

import java.util.List;

public interface BizEntityManager {

    List<BizMetaEntity> getBizMetaEntities();

    BizMetaEntity getBizMetaEntity(String name);


}
