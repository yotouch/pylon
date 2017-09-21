package com.yotouch.core.entity;

import java.util.List;

public interface MetaEntity extends Comparable<MetaEntity> {
    
    <T extends Object> MetaField<T> getMetaField(String name);
    
    List<MetaField<?>> getMetaFields();

    String getName();
    
    String getDisplayName();
    
    String getUuid();
    
    Entity newEntity();
    
    String getScope();

}
