package com.yotouch.core.entity.query.statics;

import com.yotouch.core.Consts;

public class OrderBy {
    private String order = "";
    private String by = Consts.ORDERBY_ASC;

    public OrderBy(String order) {
        this.order = order;
    }

    public OrderBy(String order, String by) {
        this.order = order;
        this.by = by;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String asSql() {
        return order + " " + by;
    }
}
