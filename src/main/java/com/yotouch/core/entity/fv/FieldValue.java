package com.yotouch.core.entity.fv;

import com.yotouch.core.entity.MetaField;

public interface FieldValue<T> extends Cloneable {
    
    MetaField<T> getField();
    
    T getValue();

    boolean isChanged();
    
    void setChanged(boolean changed);

    T getOldValue();

    void setNewValue(Object value);

    FieldValue<T> copy() throws CloneNotSupportedException;
}
