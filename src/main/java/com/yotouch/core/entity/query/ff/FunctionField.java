package com.yotouch.core.entity.query.ff;

import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.query.QueryField;

public abstract class FunctionField implements QueryField {

    protected String name;
    protected String arg;
    protected MetaFieldImpl metaField;

    public FunctionField(String name) {
        this.name = name;
    }

    public FunctionField setArg(String arg) {
        this.arg = arg;
        return this;
    }

    public FunctionField(String name, MetaFieldImpl metaField) {
        this.name = name;
        this.metaField = metaField;
    }

    public abstract String asSql();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FunctionField setMetaField(MetaFieldImpl metaField) {
        this.metaField = metaField;
        return this;
    }
}
