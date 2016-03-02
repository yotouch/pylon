package com.yotouch.core.entity.mf;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.fv.ObjectFieldValueImpl;

public class ObjectMetaFieldImpl extends MetaFieldImpl<Object> {

    public ObjectMetaFieldImpl(MetaEntity me, String uuid, String name) {
        this.me = me;
        this.uuid = uuid;
        this.name = name;
    }

    public ObjectMetaFieldImpl() {
    }

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_OBJECT;
    }

    @Override
    public FieldValue<Object> newFieldValue(Object value) {
        return new ObjectFieldValueImpl(this, value);
    }


}
