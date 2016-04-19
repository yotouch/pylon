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
    
    String WORKFLOW_STATE_TYPE_START        = "start";
    String WORKFLOW_STATE_TYPE_FINISH       = "finish";
    String WORKFLOW_STATE_TYPE_NORMAL       = "normal";
    
    String WORKFLOW_STATE_ANY_STATE         = "__ANY__";
    String WORKFLOW_STATE_SELF_STATE        = "__SELF__";
    
    String BIZ_ENTITY_FIELD_WORKFLOW        = "wf_workflow";
    String BIZ_ENTITY_FIELD_STATE           = "wf_state";
    
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
    
    
    int AJAX_STATUS_FAILED                   = 1;
    int AJAX_STATUS_OK                       = 0;

    String WALLET_TYPE_USER                 = "walletTypeUser";
    String WALLET_TYPE_SHOP                 = "walletTypeShop";

    String ROLE_CUSTOMER_NAME    = "客户";
    String ROLE_INTERVIEWEE_NAME = "应聘者";
    
}
