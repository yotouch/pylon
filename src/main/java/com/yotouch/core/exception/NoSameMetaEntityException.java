package com.yotouch.core.exception;

public class NoSameMetaEntityException extends RuntimeException {
    private String metaEntityName1;
    private String metaEntityName2;

    public NoSameMetaEntityException(String metaEntityName1, String metaEntityName2) {
        this.metaEntityName1 = metaEntityName1;
        this.metaEntityName2 = metaEntityName2;
    }

    @Override
    public String toString() {
        return "MetaEntity [" + metaEntityName2 + "] is not same as [" + metaEntityName1 + "]";
    }
}
