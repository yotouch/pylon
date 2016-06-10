package com.yotouch.base.wechat;

import java.util.Map;

import com.yotouch.core.Consts;
import com.yotouch.core.runtime.YotouchApplication;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageInterceptor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;

public class ContextInterceptor implements WxMpMessageInterceptor {


    private String appId;
    private YotouchApplication ytApp;

    public ContextInterceptor(YotouchApplication ytApp, String wechatAppId) {
        this.ytApp = ytApp;
        this.appId = wechatAppId;
    }

    @Override
    public boolean intercept(
            WxMpXmlMessage wxMessage,
            Map<String, Object> context,
            WxMpService wxMpService,
            WxSessionManager sessionManager
    ) throws WxErrorException {

        context.put(Consts.RUNTIME_VARIABLE_WX_APPID, this.appId);
        context.put(Consts.RUNTIME_VARIABLE_YT_APP, this.ytApp);

        return true;
    }
}
