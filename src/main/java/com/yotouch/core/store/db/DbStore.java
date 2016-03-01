package com.yotouch.core.store.db;

import java.util.List;
import java.util.Map;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityRowMapper;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.fv.FieldValue;

public interface DbStore {

    List<Map<String, Object>> fetchAll(MetaEntity me);

    List<Map<String, Object>> fetchList(MetaEntity me, String sql, Object[] args);

    List<String> fetchAllTables();

    void createTable(MetaEntity me);

    void alterTable(MetaEntity me);

    String insert(MetaEntity me, List<FieldValue<?>> fvs);

    void update(MetaEntity me, String uuid, List<FieldValue<?>> fvs);

    List<Entity> query(MetaEntity me, String uuid, EntityRowMapper mapper);

    List<Entity> querySql(MetaEntity me, String where, Object[] args, EntityRowMapper mapper);

    void deleteRawSql(MetaEntity me, String where, Object[] args);

}
