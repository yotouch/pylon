package com.yotouch.core.entity.fv;

import com.yotouch.core.entity.MetaField;

public class ObjectFieldValueImpl extends AbstractFieldValue<Object> implements FieldValue<Object> {

    public ObjectFieldValueImpl(MetaField<Object> mf, Object value) {
        super(mf, value);
    }

    @Override
    protected Object parseValue(Object v) {
        return v;
    }

}
