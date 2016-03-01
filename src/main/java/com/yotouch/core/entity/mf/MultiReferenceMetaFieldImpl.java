package com.yotouch.core.entity.mf;

import java.util.List;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.fv.MultiReferenceFieldValue;

public class MultiReferenceMetaFieldImpl extends ReferenceMetaFieldImpl<List<String>> {
    
    private MetaEntity mappingMe;

    public MultiReferenceMetaFieldImpl(EntityManager entityMgr, String targetMetaEntityName) {
        super(entityMgr, targetMetaEntityName);
    }

    @Override
    public String getDataType() {
        return null;
    }

    @Override
    public boolean isMultiReference() {
        return true;
    }
    
    @Override
    public String getFieldType() {
        return Consts.META_FIELD_TYPE_MULTI_REFERENCE;
    }

    @Override
    public FieldValue<List<String>> newFieldValue(Object value) {
        return new MultiReferenceFieldValue(this, value);
    }
    
    public void setMappingMetaEntity(MetaEntity me) {
        this.mappingMe = me;
    }
    
    public MetaEntity getMappingMetaEntity() {
        return mappingMe;
    }

}
