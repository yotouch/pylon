package com.yotouch.core.dao;

/**
 * Created by king on 3/29/17.
 */

import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;

import java.util.List;

public interface EntityDao<M extends EntityModel> {
    List<M> listAll(DbSession dbSession);
    M getByUuid(DbSession dbSession, String uuid);
    void deleteByUuid(DbSession dbSession, String uuid);
    M save(DbSession dbSession, M model);
    M update(DbSession dbSession, M model);
}
