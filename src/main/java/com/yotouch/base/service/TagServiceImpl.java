package com.yotouch.base.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    @Override
    public Entity addTag(DbSession dbSession, String name, String targetType, String targetUuid) {

        Entity tag = dbSession.queryOneRawSql(
                "tag",
                "targetUuid = ? AND name = ? AND targetType = ?",
                new Object[]{targetUuid, name, targetType}
        );

        if (tag == null) {
            tag = dbSession.newEntity("tag");
            tag.setValue("name", name);
            tag.setValue("targetType", targetType);
            tag.setValue("targetUuid", targetUuid);
            tag = dbSession.save(tag);
        }

        return tag;
    }

    @Override
    public List<Entity> getTargetTags(DbSession dbSession, String targetType, String targetUuid) {

        List<Entity> tags = dbSession.queryRawSql(
                "tag",
                "targetUuid = ? AND targetType = ?",
                new Object[]{targetUuid, targetType}
        );

        return tags;
    }

    @Override
    public void removeTag(DbSession dbSession, String name, String targetType, String targetUuid) {

        Entity tag = dbSession.queryOneRawSql(
                "tag",
                "targetUuid = ? AND name = ? AND targetType = ?",
                new Object[]{targetUuid, name, targetType}

        );

        if (tag != null) {
            dbSession.deleteEntity(tag);
        }

    }

    @Override
    public void setTags(DbSession dbSession, Iterable<String> tagList, String targetType, String targetUuid) {

        List<Entity> tags = this.getTargetTags(dbSession, targetType, targetUuid);

        Set<String> oldTags = tags.stream().map(t->(String)t.v("name")).collect(Collectors.toSet());
        Set<String> newTags = Sets.newHashSet(tagList);

        logger.debug("oldTags " + oldTags);
        logger.debug("newTags " + newTags);

        if (oldTags.equals(newTags)) {
            return;
        }

        Set<String> addTags = Sets.difference(newTags, oldTags);
        Set<String> removeTags = Sets.difference(oldTags, newTags);

        logger.debug("addTags " + addTags);
        for (String t: addTags) {
            this.addTag(dbSession, t, targetType, targetUuid);
        }

        logger.debug("removeTags " + removeTags);
        for (String t: removeTags) {
            this.removeTag(dbSession, t, targetType, targetUuid);
        }
    }

}
