package com.yotouch.core.entity.query;

public interface QueryField {

    String asSql();

    String getName();

    void setName(String name);

    String getDataType();
}
