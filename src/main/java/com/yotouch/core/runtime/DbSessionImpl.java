package com.yotouch.core.runtime;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yotouch.core.entity.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityImpl;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.EntityRowMapper;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaField;
import com.yotouch.core.entity.mf.MultiReferenceMetaFieldImpl;
import com.yotouch.core.store.db.DbStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "Entity")
public class DbSessionImpl implements DbSession {
    
    static final private Logger logger = LoggerFactory.getLogger(DbSessionImpl.class);

    @Autowired
    private EntityManager entityMgr;

    @Autowired
    private DbStore dbStore;

    @Value("${yotouch.entity.multiReference.lazy:}")
    private String isMrLazyStr;

    private Entity loginUser;

    private boolean isMrLazy() {
        return "1".equals(isMrLazyStr) || "true".equalsIgnoreCase(isMrLazyStr);
    }

    @Override
    public Entity newEntity(String entityName) {
        MetaEntity me = entityMgr.getMetaEntity(entityName);
        return me.newEntity();
    }

    @Override
    public Entity newEntity(String name, int status) {
        Entity e = this.newEntity(name);
        e.setValue("status", status);
        return e;
    }

    @Override
    public Entity save(Entity e) {
        String uuid = e.getUuid();

        EntityImpl ei = (EntityImpl) e;
        
        MetaEntity me = e.getMetaEntity();

        boolean isNew = ei.isNew();

        if (isNew) {
            if (this.loginUser != null) {
                e.setValue("creatorUuid", this.loginUser.getUuid());
            }
            e.setValue("createdAt", new Date());
            if (e.v("status") == null) {
                e.setValue("status", Consts.STATUS_NORMAL);
            }
            uuid = this.dbStore.insert(me, ei.getFieldValueList());
        } else {
            if (this.loginUser != null) {
                e.setValue("updaterUuid", this.loginUser.getUuid());
            }
            e.setValue("updatedAt", new Date());
            // Do Update
            this.dbStore.update(me, uuid, ei.getFieldValueList());
        }
        
        // save multi reference
        for (MetaField<?> mf : me.getMetaFields()) {
            if (mf.isMultiReference() &&
                    (e.isFieldChanged(mf.getName()) || isNew)) {
                MultiReferenceMetaFieldImpl mmf = (MultiReferenceMetaFieldImpl) mf;
                MetaEntity mappingMe = mmf.getMappingMetaEntity();
                String targetEntityName = mmf.getTargetMetaEntity().getName();

                Set<String> s1 = new HashSet<>();

                List<String> values = e.getValue(mf.getName());
                if (values == null) {
                    values = new ArrayList<>();
                }

                if (!values.isEmpty()) {
                    s1 = new HashSet<>(values);
                }

                logger.debug("Save MR " + mf.getName() + " targetMe " + mappingMe.getName() + " values " + s1);

                List<String> oldValues = e.getOldValue(mf.getName());
                if (oldValues == null) {
                    List<Entity> oldEntities = this.queryRawSql(mappingMe.getName(), "s_" + me.getName() + "Uuid = ? ORDER BY weight DESC", new Object[]{uuid});
                    oldValues = oldEntities.stream().map(o -> (String) o.v("t_" + targetEntityName + "Uuid")).collect(Collectors.toList());
                }


                Set<String> s2 = new HashSet<>();
                if (oldValues != null && !oldValues.isEmpty()) {
                    s2 = new HashSet<>(oldValues);

                    logger.debug("Set 1 and 2 " + s1 + ":" + s2 + (s1.equals(s2)));

                    if (s1.equals(s2)) {
                        continue;
                    }
                }
                logger.debug("Save MR " + mf.getName() + " old values " + s2);

                Set<String> newUuids = Sets.difference(s1, s2);
                Set<String> removeUuids = Sets.difference(s2, s1);

                for (String u : removeUuids) {
                    this.deleteRawSql(mappingMe, "s_" + me.getName() + "Uuid=? AND t_" + targetEntityName + "Uuid=?", new Object[]{uuid, u});
                }


                int weight = 0;
                Entity lastMr = this.queryOneRawSql(mappingMe.getName(), "s_" + me.getName() + "Uuid = ? ORDER BY weight DESC", new Object[]{uuid});
                if (lastMr != null) {
                    weight = lastMr.v("weight");
                }

                weight += 1;

                for (String targetUuid: values) {

                    if (!newUuids.contains(targetUuid)) {
                        continue;
                    }

                    Entity mr = this.newEntity(mappingMe.getName());
                    mr.setValue("s_" + me.getName() + "Uuid", uuid);
                    mr.setValue("t_" + targetEntityName + "Uuid", targetUuid);
                    mr.setValue("status", Consts.STATUS_NORMAL);
                    mr.setValue("weight", weight);
                    this.save(mr);

                    weight += 1;
                }
            }
        }

        

        return this.getEntity(e.getMetaEntity().getName(), uuid);
    }

    @Override
    public void deleteRawSql(MetaEntity me, String where, Object[] args) {
        this.dbStore.deleteRawSql(me, where, args);
    }



    @Override
    public void deleteEntity(MetaEntity me, String u) {
        this.dbStore.deleteRawSql(me, "uuid=?", new Object[]{u});
    }

    @Override
    public void deleteEntity(String entityName, String uuid) {
        MetaEntity me = entityMgr.getMetaEntity(entityName);
        this.deleteEntity(me, uuid);
    }

    @Override
    public void deleteEntity(Entity entity) {
        this.deleteEntity(entity.getMetaEntity(), entity.getUuid());
    }

    @Override
    //@Cacheable
    public Entity getEntity(String entityName, String uuid) {
        MetaEntity me = entityMgr.getMetaEntity(entityName);
        return this.getEntity(me, uuid);
    }
    
    @Override
    //@Cacheable
    public Entity getEntity(MetaEntity me, String uuid) {
        List<Entity> el = this.dbStore.query(me, uuid, new EntityRowMapper(this, me, isMrLazy()));
        
        if (el.isEmpty()) {
            return null;
        } else {
            return el.get(0);
        }
    }
    
    @Override
    public List<Entity> queryRawSql(String entityName, String where, Object[] args) {
        MetaEntity me = entityMgr.getMetaEntity(entityName);
        return this.dbStore.querySql(me, where, args, new EntityRowMapper(this, me, isMrLazy()));
    }

    @Override
    public List<Entity> getAll(String entityName) {
        MetaEntity me = entityMgr.getMetaEntity(entityName);
        List<Entity> el = this.dbStore.querySql(me, "", null, new EntityRowMapper(this, me, isMrLazy()));
        return el;
    }

    @Override
    public Entity queryOneRawSql(String entityName, String where, Object[] args) {

        if (!where.toLowerCase().contains("limit")) {
            where += " LIMIT 1 ";
        }

        List<Entity> el = this.queryRawSql(entityName, where, args);
        if (el.isEmpty()) {
            return null;
        }
        
        return el.get(0);
    }

    @Override
    public Entity queryOne(String entityName, Query q) {
        MetaEntity me = entityMgr.getMetaEntity(entityName);
        List<Entity> el = this.dbStore.querySql(me, q.getFields(), q.getWhere(), q.getArgs(), new EntityRowMapper(this, me, isMrLazy()));
        if (el.isEmpty()) {
            return null;
        } else {
            return el.get(0);
        }
    }

    @Override
    public List<Entity> queryIn(String entityName, List<String> entityUuids) {
        if (entityUuids.size() == 0) {
            return new ArrayList<>();
        } else {
            String[] qa = new String[entityUuids.size()];
            Arrays.fill(qa, "?");
            String where = Joiner.on(",").join(qa);

            return this.queryRawSql(entityName, " uuid IN (" + where + ")", entityUuids.toArray());
        }
    }

    @Override
    public void setLoginUser(Entity loginUser) {
        this.loginUser = loginUser;
    }

    @Override
    public Entity increase(Entity entity, String field, int amount) {
        Object o = entity.v(field);

        int v = 0;
        if (o instanceof String) {
            v = Integer.parseInt((String)o);
        } else {
            v = (int) o;
        }

        MetaEntity me = entity.getMetaEntity();
        String uuid = entity.getUuid();

        this.dbStore.increase(me, uuid, field, amount);

        Entity newEntity = this.getEntity(me, uuid);

        Object newO = newEntity.v(field);
        int newV = 0;
        if (newO instanceof String) {
            newV = Integer.parseInt((String) newO);
        } else {
            newV = (int) newO;
        }

        if (newV == v + amount) {
            return newEntity;
        } else {
            return this.increase(newEntity, field, amount);
        }

    }

    @Override
    public Entity queryOneByField(String metaEntity, String fieldName, Object value) {
        return this.queryOneRawSql(metaEntity, fieldName + "= ?", new Object[]{ value });
    }


}
