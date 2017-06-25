package com.yotouch.core.entity.query;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Query {

    private List<QueryField> fieldList;

    private String orderBy;

    private String groupBy;

    private String sql;

    private Object[] args;

    public Query() {
        this.fieldList = new ArrayList<>();
    }

    public Query addField(QueryField qf) {
        this.fieldList.add(qf);
        return this;
    }

    public Query addOrderBy(String order, String by) {
        if (StringUtils.isEmpty(this.orderBy)) {
            this.orderBy = order + " " + by.toUpperCase();
        } else {
            this.orderBy += ", " + order + " " + by.toUpperCase();
        }
        return this;
    }

    public Query addOrderBy(String order) {
        if (StringUtils.isEmpty(this.orderBy)) {
            this.orderBy = order;
        } else {
            this.orderBy += ", " + order;
        }
        return this;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public Query addGroupBy(String groupBy) {
        if (StringUtils.isEmpty(this.groupBy)) {
            this.groupBy = groupBy;
        } else {
            this.groupBy += ", " + groupBy;
        }
        return this;
    }

    public String getGroupBy() {
        return this.groupBy;
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
