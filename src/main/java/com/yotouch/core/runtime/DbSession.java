package com.yotouch.core.runtime;

import java.util.List;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.query.Query;
import com.yotouch.core.model.EntityModel;

public interface DbSession {

    Entity newEntity(String entityName);

    Entity newEntity(String entityName, int status);

    Entity save(Entity e);

    <M extends EntityModel> M save(M entityModel, String entityName);

    Entity getEntity(String entityName, String uuid);
    
    Entity getEntity(MetaEntity me, String uuid);

    <M extends EntityModel> M getEntity(String entityName, String uuid, Class<M> clazz);

    List<Entity> queryRawSql(String entityName, String where, Object[] args);

    <M extends EntityModel> List<M> queryRawSql(String entityName, String where, Object[] args, Class<M> clazz);

    Entity queryOneRawSql(String entityName, String where, Object[] args);

    <M extends EntityModel> M queryOneRawSql(String entityName, String where, Object[] args, Class<M> clazz);

    List<Entity> getAll(String string);

    <M extends EntityModel> List<M> getAll(String string, Class<M> clazz);

    void deleteEntity(MetaEntity me, String uuid);

    void deleteEntity(String entityName, String uuid);

    void deleteEntity(Entity entity);

    void deleteRawSql(MetaEntity me, String where, Object[] args);

    Entity queryOne(String entityName, Query q);

    List<Entity> queryIn(String entityName, List<String> entityUuids);

    void setLoginUser(Entity loginUser);

    Entity increase(Entity entity, String field, int amount);

    Entity queryOneByField(String metaEntity, String fieldName, Object value);
}
