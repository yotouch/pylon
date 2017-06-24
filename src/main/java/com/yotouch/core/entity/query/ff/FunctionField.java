package com.yotouch.core.entity.query.ff;

import com.yotouch.core.entity.query.QueryField;

public abstract class FunctionField implements QueryField {

    protected String name;
    protected String arg;

    public FunctionField(String name) {
        this.name = name;
    }

    public FunctionField setArg(String arg) {
        this.arg = arg;
        return this;
    }

    public abstract String asSql();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
