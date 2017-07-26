package com.yotouch.core.dao;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by king on 3/29/17.
 */
public class EntityDaoImpl<M extends EntityModel> implements EntityDao<M> {

    private Class<M> clazz;

    private String entityName;

    public EntityDaoImpl(Class<M> clazz, String entityName) {
        this.clazz = clazz;
        this.entityName = entityName;
    }

    @Override
    public List<M> listAll(DbSession dbSession) {
        return dbSession.getAll(entityName, clazz);
    }



    @Override
    public M getByUuid(DbSession dbSession, String uuid) {
        return dbSession.getEntity(entityName, uuid, clazz);
    }

    @Override
    public void deleteByUuid(DbSession dbSession, String uuid) {
        M model = getByUuid(dbSession, uuid);
        if (model != null && !StringUtils.isEmpty(model.getUuid())) {
            model.setStatus(Consts.STATUS_DELETED);
            save(dbSession, model);
        }
    }

    @Override
    public M save(DbSession dbSession, M model) {
        return dbSession.save(model, entityName);
    }

    @Override
    public M update(DbSession dbSession, M model) {
        return save(dbSession, model);
    }
}
