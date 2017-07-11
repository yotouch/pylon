package com.yotouch.core.entity;

import java.util.List;

import com.yotouch.core.entity.MetaEntity;

public interface EntityManager {

    List<MetaEntity> getMetaEntities();

    MetaEntity getMetaEntity(String name);

    void reload();

}
