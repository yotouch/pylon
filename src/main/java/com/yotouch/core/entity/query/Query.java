package com.yotouch.core.entity.query;

import com.yotouch.core.entity.query.statics.GroupBy;
import com.yotouch.core.entity.query.statics.OrderBy;

import java.util.ArrayList;
import java.util.List;

public class Query {

    private List<QueryField> fieldList;

    private List<OrderBy> orderByList = new ArrayList<>();

    private List<GroupBy> groupByList = new ArrayList<>();

    private String sql;

    private Object[] args;

    public Query() {
        this.fieldList = new ArrayList<>();
    }

    public Query addField(QueryField qf) {
        this.fieldList.add(qf);
        return this;
    }

    public Query addOrderBy(OrderBy orderBy) {
        this.orderByList.add(orderBy);
        return this;
    }

    public String genOrderByString() {
        if (orderByList.isEmpty()) {
            return "";
        }

        StringBuilder orders = new StringBuilder();
        for (OrderBy orderBy : orderByList) {
            orders.append(orderBy.asSql()).append(", ");
        }

        return orders.substring(0, orders.length() - 2);
    }

    public Query addGroupBy(GroupBy groupBy) {
        this.groupByList.add(groupBy);
        return this;
    }

    public String genGroupByString() {
        if (groupByList.isEmpty()) {
            return "";
        }

        StringBuilder groups = new StringBuilder();
        for (GroupBy groupBy : groupByList) {
            groups.append(groupBy.asSql()).append(", ");
        }

        return groups.substring(0, groups.length() - 2);
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
