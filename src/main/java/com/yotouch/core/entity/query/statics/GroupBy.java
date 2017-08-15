package com.yotouch.core.entity.query.statics;

public class GroupBy {
    private String groupBy;

    public GroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String asSql() {
        return groupBy;
    }
}
