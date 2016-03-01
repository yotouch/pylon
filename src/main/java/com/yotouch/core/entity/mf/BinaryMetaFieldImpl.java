package com.yotouch.core.entity.mf;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.fv.BinaryFieldValueImpl;
import com.yotouch.core.entity.fv.FieldValue;

public class BinaryMetaFieldImpl extends MetaFieldImpl<byte[]> {

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_BINARY;
    }

    

    @Override
    public FieldValue<byte[]> newFieldValue(Object value) {
        return new BinaryFieldValueImpl(this, value);
    }

}
