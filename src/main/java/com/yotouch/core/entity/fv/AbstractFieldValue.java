package com.yotouch.core.entity.fv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.yotouch.core.entity.MetaField;

public abstract class AbstractFieldValue<T> implements FieldValue<T> {
    
    static final private Logger logger = LoggerFactory.getLogger(AbstractFieldValue.class);

    private MetaField<T> mf;

    private T value;
    
    private T oldValue;

    private boolean changed;
    
    public AbstractFieldValue(MetaField<T> mf, Object value) {
        this.mf = mf;
        this.changed = false;

        this.setValue(value);
    }
    
    private void setValue(Object v) {
        this.value = this.parseValue(v);
    }

    @Override
    public MetaField<T> getField() {
        return this.mf;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public boolean isChanged() {
        return this.changed;
    }
    
    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public T getOldValue() {
        if (this.isChanged()) {
            return this.oldValue;
        }
        
        return null;
    }

    public void setNewValue(Object v) {
        T value = this.parseValue(v);
        if (!this.isChanged()) {
            if (Objects.equal(value, this.value)) {
                return;
            }
        }

        this.oldValue = this.value;
        this.value = value;
        this.changed = true;
    }

    abstract protected T parseValue(Object v);

    @Override
    public FieldValue<T> copy() throws CloneNotSupportedException {
        return (FieldValue<T>) this.clone();
    }

}
