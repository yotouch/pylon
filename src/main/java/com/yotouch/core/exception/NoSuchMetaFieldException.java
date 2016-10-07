package com.yotouch.core.exception;

import com.yotouch.core.entity.MetaEntity;

public class NoSuchMetaFieldException extends RuntimeException {
    
    private static final long serialVersionUID = -1437118192380271388L;

    
    private MetaEntity me;
    private String fieldName;

    public NoSuchMetaFieldException(MetaEntity me, String fieldName) {
        this.me = me;
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "NoSuchMetaFieldException [MetaEntity=" + me.getName() + ", fieldName=" + fieldName + "]";
    }
    
    

}
