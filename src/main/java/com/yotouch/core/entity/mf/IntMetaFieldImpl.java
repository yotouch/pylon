package com.yotouch.core.entity.mf;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.fv.IntFieldValueImpl;

public class IntMetaFieldImpl extends MetaFieldImpl<Integer> {
    
    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_INT;
    }

    @Override
    public FieldValue<Integer> newFieldValue(Object value) {
        return new IntFieldValueImpl(this, value);
    }

}
