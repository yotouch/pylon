package com.yotouch.core.entity.mf;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.fv.StringFieldValueImpl;

public class SingleReferenceMetaFieldImpl extends ReferenceMetaFieldImpl<String> {


    public SingleReferenceMetaFieldImpl(EntityManager entityMgr, String targetMetaEntityName) {
        super(entityMgr, targetMetaEntityName);
    }

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_UUID;
    }

    @Override
    public boolean isSingleReference() {
        return true;
    }
    
    @Override
    public String getFieldType() {
        return Consts.META_FIELD_TYPE_SINGLE_REFERENCE;
    }

    @Override
    public FieldValue<String> newFieldValue(Object value) {
        return new StringFieldValueImpl(this, value, true);
    }
    
    

}
