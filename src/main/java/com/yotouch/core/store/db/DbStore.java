package com.yotouch.core.store.db;

import java.util.List;
import java.util.Map;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityRowMapper;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.query.Query;
import com.yotouch.core.entity.query.QueryField;

public interface DbStore {

    List<Map<String, Object>> fetchAll(MetaEntity me);

    List<Map<String, Object>> fetchList(MetaEntity me, String sql, Object[] args);

    List<String> fetchAllTables(boolean toLower);

    void createTable(MetaEntity me);

    void alterTable(MetaEntity me);

    String insert(MetaEntity me, List<FieldValue<?>> fvs);

    String insert(MetaEntity me, List<FieldValue<?>> fvs, String uuid);

    void update(MetaEntity me, String uuid, List<FieldValue<?>> fvs);

    List<Entity> query(MetaEntity me, String uuid, EntityRowMapper mapper);

    List<Entity> querySql(MetaEntity me, String where, Object[] args, EntityRowMapper mapper);

    List<Entity> querySql(MetaEntity me, List<QueryField> fields, String where, Object[] args, EntityRowMapper entityRowMapper);

    List<Entity> query(MetaEntity me, Query query, EntityRowMapper entityRowMapper);

    void increase(MetaEntity me, String uuid, String field, int amount);

    void deleteRawSql(MetaEntity me, String where, Object[] args);


}
