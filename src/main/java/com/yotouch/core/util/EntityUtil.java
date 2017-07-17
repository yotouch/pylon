package com.yotouch.core.util;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by king on 6/6/17.
 */
@Component
public class EntityUtil {
    private static EntityManager entityManager;

    private static ApplicationContext ctx;

    private static DbSession dbSession;

    @Autowired
    public EntityUtil(ApplicationContext ctx, DbSession dbSession, EntityManager entityManager) {
        EntityUtil.ctx = ctx;
        EntityUtil.dbSession = dbSession;
        EntityUtil.entityManager = entityManager;
    }

    public static Entity convert(EntityModel entityModel) {
        String modelName = entityModel.getClass().getSimpleName();
        String entityName = StrUtil.modelNameToEntityName(modelName);

        Entity entity = entityManager.getMetaEntity(entityName).newEntity();

        return entity.fromModel(entityModel);
    }

    public static  <M extends EntityModel> M convert(Entity entity) {
        String entityName = entity.getMetaEntity().getName();
        M entityModel = (M)ctx.getBean(entityName);
        return Entity.looksLike(dbSession, entity, (Class<M>) entityModel.getClass());
    }

    public static <M extends EntityModel> M getEntityModel(String modelName) {
       return (M)ctx.getBean(modelName);
    }
}
