package com.yotouch.core;

public interface Consts {

    String META_FIELD_DATA_TYPE_STRING      = "STRING";
    String META_FIELD_DATA_TYPE_UUID        = "UUID";
    String META_FIELD_DATA_TYPE_DATETIME    = "DATETIME";
    String META_FIELD_DATA_TYPE_OBJECT      = "OBJECT";
    String META_FIELD_DATA_TYPE_INT         = "INT";
    String META_FIELD_DATA_TYPE_LONG        = "LONG";
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

    String WORKFLOW_ACTION_TYPE_TO_SELF     = "to_self";
    String WORKFLOW_ACTION_TYPE_NORMAL      = "normal";

    String WORKFLOW_ACTION_LOG_NOTE         = "ACTION_LOG_NOTE";

    String WORKFLOW_ACTION_EXTRA_DIFF_FIELD_NAME = "fieldName";
    String WORKFLOW_ACTION_EXTRA_DIFF_OLD_VALUE  = "oldValue";
    String WORKFLOW_ACTION_EXTRA_DIFF_NEW_VALUE  = "newValue";


    String BIZ_ENTITY_FIELD_WORKFLOW        = "wf_workflow";
    String BIZ_ENTITY_FIELD_STATE           = "wf_state";

    String CONFIG_KEY_INSTANCE_NAME         = "instanceName";
    String CONFIG_KEY_CONST                 = "const";

    String META_FIELD_TYPE_DATA_FIELD       = "DATA_FIELD";
    String META_FIELD_TYPE_SINGLE_REFERENCE = "SINGLE_REFERENCE";
    String META_FIELD_TYPE_MULTI_REFERENCE  = "MULTI_REFERENCE";

    int STATUS_NORMAL                       = 1000;
    int STATUS_DELETED                      = 1001;
    int STATUS_LOCKING                      = 1004;
    int STATUS_SHADOW                       = 404;

    int STATUS_PAYMENT_DEPOSIT_PENDING      = 6001;   // 钱等待存入
    int STATUS_PAYMENT_DEPOSIT_FAILED       = 6010;   // 钱等待存入
    int STATUS_PAYMENT_DEPOSITED            = 6050;   // 钱已经存入
    int STATUS_PAYMENT_WITHDRAWN            = 6100;   // 钱已经支出

    String WECHAT_STATE_REDIRECT            = "urlRedirect";

    String COOKIE_NAME_WX_USER_UUID         = "wxUserUuid";
    String COOKIE_NAME_WECHAT_OPENID        = "wechatOpenId";

    String RUNTIME_VARIABLE_WECHAT_USER     = "WECHAT_USER";
    String RUNTIME_VARIABLE_WX_APPID        = "WECHAT_APPID";
    String RUNTIME_VARIABLE_WX_JS_ARGS      = "WECHAT_JS_ARGS";
    String RUNTIME_VARIABLE_WX_SERVICE      = "WECHAT_SERVICE";
    String RUNTIME_VARIABLE_BROWSER_WECHAT  = "BROWSER_WECHAT";
    String RUNTIME_VARIABLE_YT_APP          = "YT_APP";
    String RUNTIME_VARIABLE_APP_HOST        = "APP_HOST";
    String RUNTIME_VARIABLE_CUSTOMER        = "CUSTOMER";
    String RUNTIME_VARIABLE_USER            = "USER";

    String CASHFLOW_SCENE_PAY_ORDER            = "SCENE_PAY_ORDER";            // 支付到订单
    String CASHFLOW_SCENE_PAY_COMMISSION       = "SCENE_PAY_COMMISSION";       // 支付佣金
    String CASHFLOW_SCENE_CUSTOMER_WITHDRAW    = "SCENE_CUSTOMER_WITHDRAW";    // 客户取款
    String CASHFLOW_SCENE_CONFIRM_SUBORDER     = "SCENE_CONFIRM_SUBORDER";     // 结算到子订单
    String CASHFLOW_SCENE_SETTLE_TO_ORDER_ITEM = "SCENE_SETTLE_TO_ORDER_ITEM"; // 订单拆分结算到 orderItem
    String CASHFLOW_SCENE_SETTLE_TO_SHOP       = "SCENE_SETTLE_TO_SHOP";       // 结算到商店
    String CASHFLOW_SCENE_REFUND_ORDER         = "SCENE_REFUND_ORDER";         // 订单退款

    String CASHFLOW_TYPE_WECHAT             = "CF_TYPE_WECHAT";
    String CASHFLOW_TYPE_ORDER              = "CF_TYPE_ORDER";
    String CASHFLOW_TYPE_ORDER_ITEM         = "CF_TYPE_ORDER_ITEM";
    String CASHFLOW_TYPE_SHOP               = "CF_TYPE_SHOP";
    String CASHFLOW_TYPE_CUSTOMER           = "CF_TYPE_CUSTOMER";

    String PAYMENT_VENDOR_WECHAT            = "PAYMENT_WECHAT";
    String PAYMENT_VENDOR_WECHAT_MCH        = "PAYMENT_WECHAT_MCH";
    String PAYMENT_VENDOR_ALIPAY            = "PAYMENT_ALIPAY";

    String REFUND_VENDOR_WECHAT             = "REFUND_VENDOR_WECHAT";
    String REFUND_VENDOR_WECHAT_MCH = "REFUND_VENDOR_WECHAT_MCH";

    String WECHAT_TRADE_STATUS_SUCCESS      = "SUCCESS";
    String WECHAT_TRADE_STATUS_NOTPAY       = "NOTPAY";

    int AJAX_STATUS_FAILED                  = 1;
    int AJAX_STATUS_OK                      = 0;

    String WALLET_TYPE_CUSTOMER             = "WALLET_TYPE_CUSTOMER";
    String WALLET_TYPE_SHOP                 = "WALLET_TYPE_SHOP";

    String ROLE_EMPLOYEE                    = "员工";
    String ROLE_USER                        = "用户";
    String ROLE_EMPLOYEE_ADMIN              = "管理员";

    int DUPLICATE_NAME                       = 5400; // 用户名重复
    int PWD_UNCONSISTENCY                    = 5401; // 重复密码不一致
    int DUPLICATE_PHONE                      = 5410; // 电话重复

    int paginationWidth                     = 5;
    int itemPerPage                         = 20;


    int REFUND_TYPE_ORDER  = 5510;
    int REFUND_TYPE_ITEMS  = 5520;
    int REFUND_TYPE_AMOUNT = 5530;

    int YSE                                 = 1;
    int NO                                  = 0;

    String ORDERBY_DESC                     = "DESC";
    String ORDERBY_ASC                      = "ASC";

}
