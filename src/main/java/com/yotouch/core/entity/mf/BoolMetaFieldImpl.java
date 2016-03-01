package com.yotouch.core.entity.mf;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.fv.BoolFieldValueImpl;
import com.yotouch.core.entity.fv.FieldValue;

public class BoolMetaFieldImpl extends MetaFieldImpl<Boolean> {

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_BOOLEAN;
    }


    @Override
    public FieldValue<Boolean> newFieldValue(Object value) {
        return new BoolFieldValueImpl(this, value);
    }

}
