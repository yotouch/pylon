package com.yotouch.core.entity;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.mf.StringMetaFieldImpl;

public class UuidMetaFieldImpl extends StringMetaFieldImpl {

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_UUID;
    }


}
