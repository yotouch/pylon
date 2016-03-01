package com.yotouch.core.entity.mf;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.fv.DoubleFieldValueImpl;
import com.yotouch.core.entity.fv.FieldValue;

public class DoubleMetaFieldImpl extends MetaFieldImpl<Double> {

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_DOUBLE;
    }

    

    @Override
    public FieldValue<Double> newFieldValue(Object value) {
        return new DoubleFieldValueImpl(this, value);
    }

}
