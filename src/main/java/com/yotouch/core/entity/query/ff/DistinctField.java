package com.yotouch.core.entity.query.ff;

import com.yotouch.core.entity.MetaField;

public class DistinctField extends FunctionField {

    public DistinctField(String name, MetaField metaField) {
        super(name);
    }

    public DistinctField(String name) {
        super(name);
    }

    public DistinctField() {
        super("func_distinct");
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
