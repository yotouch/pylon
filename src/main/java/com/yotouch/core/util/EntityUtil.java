package com.yotouch.core.util;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.model.EntityModel;
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

    @Autowired
    public EntityUtil(ApplicationContext ctx, EntityManager entityManager) {
        EntityUtil.ctx = ctx;
        EntityUtil.entityManager = entityManager;
    }

    public static Entity convert(EntityModel entityModel) {
        String modelName = entityModel.getClass().getSimpleName();
        String entityName = StrUtil.modelNameToEntityName(modelName);

        return entityManager.getMetaEntity(entityName).newEntity().fromModel(entityModel);
    }

    public static  <M extends EntityModel> M convert(Entity entity) {
        String entityName = entity.getMetaEntity().getName();
        M entityModel = (M)ctx.getBean(entityName);
        return entity.looksLike((Class<M>) entityModel.getClass());
    }
}
