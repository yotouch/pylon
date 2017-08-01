package com.yotouch.core.dao;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by king on 3/29/17.
 */
public class EntityDaoImpl<M extends EntityModel> implements EntityDao<M> {
    @Autowired
    private DbSession dbSession;

    private Class<M> clazz;

    private String entityName;

    public EntityDaoImpl(Class<M> clazz, String entityName) {
        this.clazz = clazz;
        this.entityName = entityName;
    }

    @Override
    public List<M> listAll() {
        return dbSession.getAll(entityName, clazz);
    }

    @Override
    public M getByUuid(String uuid) {
        Entity entity = dbSession.getEntity(entityName, uuid);
        return entity == null ? null : Entity.looksLike(dbSession, entity, clazz);
    }

    @Override
    public void deleteByUuid(String uuid) {
        M model = getByUuid(uuid);
        if (model != null && !StringUtils.isEmpty(model.getUuid())) {
            model.setStatus(Consts.STATUS_DELETED);
            save(model);
        }
    }

    @Override
    public M save(M model) {
        return dbSession.save(model, entityName);
    }

    @Override
    public M update(M model) {
        return save(model);
    }
}
