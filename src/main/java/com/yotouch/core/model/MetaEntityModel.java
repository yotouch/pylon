package com.yotouch.core.model;

import org.springframework.stereotype.Component;

/**
 * Created by tammy on 09/07/2017.
 */
@Component
public class MetaEntityModel extends EntityModel {
    private String  displayName;
    private String  name;
    private String  scope;
    private Integer seqId;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(Integer seqId) {
        this.seqId = seqId;
    }
}
