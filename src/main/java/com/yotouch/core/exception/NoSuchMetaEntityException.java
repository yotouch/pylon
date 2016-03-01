package com.yotouch.core.exception;

public class NoSuchMetaEntityException extends RuntimeException {

    private static final long serialVersionUID = 2356656582497456566L;
    
    private String entityName;
    
    public NoSuchMetaEntityException(String entityName) {
        this.entityName = entityName;
    }
    
    public String getMessage() {
        return this.toString();
    };

    @Override
    public String toString() {
        return "NoSuchMetaEntityException [entityName=" + entityName + "]";
    }
    
    

}
