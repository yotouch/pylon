package com.yotouch.core.util;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;

import java.util.List;

public class EntityUtil {
    public static List<Entity> rsr(DbSession dbSession, String childEntityName, String childSrFieldName, String fatherUuid) {
        return dbSession.queryRawSql(
                childEntityName,
                childSrFieldName + "Uuid = ? ORDER BY createdAt DESC",
                new Object[]{fatherUuid}
        );
    }

    public <C extends EntityModel> List<C> listChild(DbSession dbSession, String childEntityName, String childSrFieldName, String fatherUuid, Class<C> childClass) {
        return dbSession.queryRawSql(
                childEntityName,
                childSrFieldName + "Uuid = ? ORDER BY createdAt DESC",
                new Object[]{fatherUuid},
                childClass
        );
    }
}
