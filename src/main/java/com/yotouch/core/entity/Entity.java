package com.yotouch.core.entity;

import java.util.List;
import java.util.Map;

import com.yotouch.core.exception.NoSuchMetaFieldException;
import com.yotouch.core.runtime.DbSession;

public interface Entity {
    
    MetaEntity getMetaEntity();

    String getUuid();
    
    boolean isNew();

    <T extends Object> void setValue(String fieldName, Object value);

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
}
