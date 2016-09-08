package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

import java.util.List;

public interface TagService {

    Entity addTag(DbSession dbSession, String name, String targetType, String targetUuid);

    List<Entity> getTargetTags(DbSession dbSession, String targetType, String targetUuid);

    void removeTag(DbSession dbSession, String name, String targetType, String targetUuid);

    void setTags(DbSession dbSession, Iterable<String> tagList, String targetType, String targetUuid);
}
