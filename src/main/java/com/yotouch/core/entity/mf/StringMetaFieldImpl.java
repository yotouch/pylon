package com.yotouch.core.entity.mf;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.fv.StringFieldValueImpl;

public class StringMetaFieldImpl extends MetaFieldImpl<String> {

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_STRING;
    }

    @Override
    public FieldValue<String> newFieldValue(Object value) {
        return new StringFieldValueImpl(this, value);
    }

}
