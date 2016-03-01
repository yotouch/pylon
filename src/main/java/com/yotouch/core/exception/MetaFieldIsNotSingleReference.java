package com.yotouch.core.exception;

import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaField;

public class MetaFieldIsNotSingleReference extends RuntimeException {
    
    private static final long serialVersionUID = -5064380760771363505L;
    
    private MetaEntity me;
    private MetaField<?> mf;

    public MetaFieldIsNotSingleReference(MetaEntity me, MetaField<?> mf) {
        this.me = me;
        this.mf = mf;
    }

    @Override
    public String toString() {
        return "MetaFieldIsNotSingleReference [me=" + me + ", mf=" + mf + "]";
    }
    
}
