package com.yotouch.core.entity.query;

import java.util.ArrayList;
import java.util.List;

public class Query {

    private List<QueryField> fieldList;

    private String sql;

    private Object[] args;

    public Query() {
        this.fieldList = new ArrayList<>();
    }

    public Query addField(QueryField qf) {
        this.fieldList.add(qf);
        return this;
    }

    public Query rawSql(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
        return this;
    }

    public List<QueryField> getFields() {
        return new ArrayList<>(this.fieldList);
    }

    public String getWhere() {
        return sql;
    }

    public Object[] getArgs() {
        return args;
    }
}
