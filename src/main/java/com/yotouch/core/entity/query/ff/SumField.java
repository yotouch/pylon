package com.yotouch.core.entity.query.ff;

import com.yotouch.core.Consts;

public class SumField extends FunctionField {

    public SumField(String name) {
        super(name);
    }

    public SumField() {
        super("func_sum");
    }

    @Override
    public String asSql() {
        return "SUM(" + this.arg + ") as " + this.getName();
    }

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_DOUBLE;
    }
}
