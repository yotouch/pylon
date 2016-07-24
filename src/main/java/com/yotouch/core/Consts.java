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
    int STATUS_SHADOW                       = 404;
    int STATUS_PAYMENT_DEPOSIT_PENDING      = 6001;   // 钱等待存入
    int STATUS_PAYMENT_DEPOSITED            = 6050;   // 钱已经存入
    int STATUS_PAYMENT_WITHDRAWN            = 6100;   // 钱已经支出
    
    String WECHAT_STATE_REDIRECT            = "urlRedirect";
    
    String COOKIE_NAME_WX_USER_UUID         = "wxUserUuid";
    
    String RUNTIME_VARIABLE_WECHAT_USER     = "WECHAT_USER";
    String RUNTIME_VARIABLE_WX_APPID        = "WECHAT_APPID";
    String RUNTIME_VARIABLE_WX_JS_ARGS      = "WECHAT_JS_ARGS";
    String RUNTIME_VARIABLE_WX_SERVICE      = "WECHAT_SERVICE";
    String RUNTIME_VARIABLE_YT_APP          = "YT_APP";
    String RUNTIME_VARIABLE_APP_HOST        = "APP_HOST";
    String RUNTIME_VARIABLE_CUSTOMER        = "CUSTOMER";

    String CASHFLOW_SCENE_PAY_ORDER         = "SCENE_PAY_ORDER";
    String CASHFLOW_SCENE_PAY_COMMISSION    = "SCENE_PAY_COMMISSION";
    String CASHFLOW_SCENE_CUSTOMER_WITHDRAW = "SCENE_CUSTOMER_WITHDRAW";
    String CASHFLOW_SCENE_SETTLE_ORDER      = "SCENE_SETTLE_ORDER";

    String CASHFLOW_TYPE_WECHAT             = "CF_TYPE_WECHAT";
    String CASHFLOW_TYPE_ORDER              = "CF_TYPE_ORDER";
    String CASHFLOW_TYPE_SHOP               = "CF_TYPE_SHOP";
    String CASHFLOW_TYPE_CUSTOMER           = "CF_TYPE_CUSTOMER";

    String PAYMENT_VENDOR_WECHAT            = "PAYMENT_WECHAT";
    String WECHAT_TRADE_STATUS_SUCCESS      = "SUCCESS";
    
    int AJAX_STATUS_FAILED                  = 1;
    int AJAX_STATUS_OK                      = 0;

    String WALLET_TYPE_CUSTOMER             = "WALLET_TYPE_CUSTOMER";
    String WALLET_TYPE_SHOP                 = "WALLET_TYPE_SHOP";

    String ROLE_EMPLOYEE                    = "员工";
    String ROLE_USER                        = "用户";

    int paginationWidth                     = 5;
    int itemPerPage                         = 20;



}
