package com.yotouch.core.exception;

import com.yotouch.core.entity.MetaField;

/**
 * Created by king on 7/4/17.
 */
public class MetaFieldHasNoMetaEntityException extends RuntimeException {
    private static final long serialVersionUID = 6368378123182885673L;


    private String fieldName;

    public MetaFieldHasNoMetaEntityException(MetaField metaField) {
        this.fieldName = metaField.getName();
    }

    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "MetaFieldHasNoMetaEntityException [MetaField=" + fieldName + "]";
    }

}
