package com.yotouch.core;

public interface Consts {

    String META_FIELD_DATA_TYPE_STRING      = "STRING";
    String META_FIELD_DATA_TYPE_UUID        = "UUID";
    String META_FIELD_DATA_TYPE_DATETIME    = "DATETIME";
    String META_FIELD_DATA_TYPE_OBJECT      = "OBJECT";
    String META_FIELD_DATA_TYPE_INT         = "INT";
    String META_FIELD_DATA_TYPE_DOUBLE      = "DOUBLE";
    String META_FIELD_DATA_TYPE_TEXT        = "TEXT";
    String META_FIELD_DATA_TYPE_BINARY      = "BINARY";
    String META_FIELD_DATA_TYPE_BOOLEAN     = "BOOL";
    
    String FIELD_VARIABLE_NOW               = "${NOW}";
    
    String DEFAULT_COMPANY_NAME             = "default";
    
    String FIELD_NAME_COMPANY_UUID          = "companyUuid";
    
    String CONFIG_KEY_INSTANCE_NAME         = "instanceName";
    String CONFIG_KEY_CONST                 = "const";
    
    String META_FIELD_TYPE_DATA_FIELD       = "DATA_FIELD";
    String META_FIELD_TYPE_SINGLE_REFERENCE = "SINGLE_REFERENCE";
    String META_FIELD_TYPE_MULTI_REFERENCE  = "MULTI_REFERENCE";
    
    int STATUS_NORMAL                       = 1000;
    int STATUS_DELETED                      = 1001;
    int STATUS_PAYMENT_CONFIRM_PENDING      = 6001;
    int STATUS_PAYMENT_SUCCESS              = 6002;
    
    String WECHAT_STATE_REDIRECT            = "urlRedirect";
    
    String COOKIE_NAME_WX_USER_UUID         = "wxUserUuid";
    
    String RUNTIME_VARIABLE_WX_USER         = "wxUser";
    String RUNTIME_VARIABLE_WX_APPID        = "wechatAppId";
    String RUNTIME_VARIABLE_WX_JS_ARGS      = "wechatJsArgs";
    
    
    String PAYMENT_VENDOR_WECHAT            = "payment_wechat";
    String WECHAT_TRADE_STATUS_SUCCESS      = "SUCCESS";
    
    
    
}
