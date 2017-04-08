package com.yotouch.core.dao;

/**
 * Created by king on 3/29/17.
 */

import com.yotouch.core.model.EntityModel;

import java.util.List;

public interface EntityDao<M extends EntityModel> {
    List<M> listAll();
    M getByUuid(String uuid);
    void deleteByUuid(String uuid);
    M save(M model);
    M update(M model);
}
