package com.yotouch.core.entity.mf;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.fv.LongFieldValueImpl;

public class LongMetaFieldImpl extends MetaFieldImpl<Long> {
    
    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_LONG;
    }

    @Override
    public FieldValue<Long> newFieldValue(Object value) {
        return new LongFieldValueImpl(this, value);
    }

}
