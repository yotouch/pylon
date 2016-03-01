package com.yotouch.core.entity;

import com.yotouch.core.entity.fv.FieldValue;

public interface MetaField<T> {
    
    String getName();
    
    String getDisplayName();
    
    String getFieldType();
    
    String getDataType();
    
    boolean isRequired();
    
    T getDefaultValue();
    
    String getUuid();
    
    MetaEntity getMetaEntity();
    
    boolean isReference();

    boolean isMultiReference();
    
    MetaEntity getTargetMetaEntity();
    
    boolean isSingleReference();

    FieldValue<T> newFieldValue(Object value);
    
}
