package com.yotouch.core.entity.query.ff;

import com.yotouch.core.Consts;

public class CountField extends FunctionField {

    public CountField() {
        super("func_count");
    }

    @Override
    public String asSql() {
        return "COUNT(*) as " + this.getName();
    }

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_INT;
    }

}
