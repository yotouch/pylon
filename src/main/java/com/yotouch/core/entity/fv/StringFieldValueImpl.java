package com.yotouch.core.entity.fv;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.MetaField;

public class StringFieldValueImpl extends AbstractFieldValue<String> implements FieldValue<String> {

    private boolean isSr;

    public StringFieldValueImpl(MetaField<String> mf, Object value) {
        this(mf, value, false);
    }
    
    public StringFieldValueImpl(MetaField<String> mf, Object value, boolean isSr) {
        super(mf, value);
        this.isSr = isSr;
    }

    @Override
    protected String parseValue(Object v) {
        if (v == null) {
            return "";
        } else if (v instanceof Entity && this.isSr) {
            Entity e = (Entity) v;
            return e.getUuid();
        } else {
            return v.toString();
        }

    }

}
