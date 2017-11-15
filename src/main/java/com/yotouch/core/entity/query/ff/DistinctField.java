package com.yotouch.core.entity.query.ff;

import com.yotouch.core.entity.MetaFieldImpl;

public class DistinctField extends FunctionField {

    public DistinctField(String name, MetaFieldImpl metaField) {
        super(name, metaField);
    }

    @Override
    public String asSql() {
        return "DISTINCT " + this.arg + " AS " + this.getName();
    }

    @Override
    public String getDataType() {
        return this.metaField.getDataType();
    }
}
