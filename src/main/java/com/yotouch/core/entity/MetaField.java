package com.yotouch.core.entity;

import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.query.QueryField;

import java.util.List;


public interface MetaField<T> extends QueryField {
    
    String getType();
    
    String getName();
    
    String getDisplayName();
    
    String getFieldType();
    
    String getDataType();
    
    boolean isRequired();
    
    T getDefaultValue();

    FieldValue<T> getDefaultFieldValue();
    
    String getUuid();
    
    MetaEntity getMetaEntity();
    
    boolean isReference();

    boolean isMultiReference();
    
    MetaEntity getTargetMetaEntity();
    
    boolean isSingleReference();

    ValueOption getValueOption(String optionValueDisplayname);

    List<ValueOption> getValueOptions();

    FieldValue<T> newFieldValue(Object value);
    
}
