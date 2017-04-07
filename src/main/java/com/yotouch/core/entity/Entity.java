package com.yotouch.core.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yotouch.core.Consts;
import com.yotouch.core.exception.NoSuchMetaFieldException;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
public interface Entity {
    
    MetaEntity getMetaEntity();

    String getUuid();
    
    boolean isNew();

    <T extends Object> Entity setValue(String fieldName, Object value);
    
    <T extends Object> T getValue(DbSession dbSession, String field);
    
    <T extends Object> T v(DbSession dbSession, String field);

    <T extends Object> T getValue(String fieldName) throws NoSuchMetaFieldException;
    
    /*
     * Nickname for getValue
     */
    <T extends Object> T v(String fieldName) throws NoSuchMetaFieldException;

    // Check if field changed, always means set new value
    boolean isFieldChanged(String fieldName);
    
    // Get previous value after setting new value
    <T extends Object> T getOldValue(String fieldName);

    Map<String, Object> valueMap();
    
    Map<String, Object> extraValueMap(DbSession dbSession);
    
    List<Entity> getMultiReference(DbSession dbSession, String fieldName);
    /*
     * Nickname for getMultiReference
     */
    List<Entity> mr(DbSession dbSession, String fieldName);

    
    Entity getSingleReference(DbSession dbSession, String fieldName);
    /*
     * Nickname for getSingleReference
     */
    Entity sr(DbSession dbSession, String fieldName);

    Map<String, Object> asMap();

    static Map<String, Object> asMap(DbSession dbSession, Entity entity){
        if (entity == null){
            return null;
        }

        Map<String, Object> m = new HashMap<>();
        for (MetaField<?> mf : entity.getMetaEntity().getMetaFields()){
            if (mf.getFieldType().equals(Consts.META_FIELD_TYPE_DATA_FIELD)) {
                m.put(mf.getName(), entity.v(mf.getName()));
            } else if (mf.isSingleReference()) {
                m.put(mf.getName(), Entity.asMap(dbSession, entity.sr(dbSession, mf.getName())));
            }
        }
        return m;
    }

    <T extends EntityModel> T looksLike(Class<T> clazz);

    static  <T extends EntityModel> T looksLike(DbSession dbSession, Entity entity, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(Entity.asMap(dbSession, entity), clazz);
    }

    <T extends EntityModel> Entity fromModel(T entityModel);

    Entity fromMap(Map<String, Object> map);
}
