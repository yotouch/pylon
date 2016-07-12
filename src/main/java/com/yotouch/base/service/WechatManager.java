package com.yotouch.base.service;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;

public interface WechatManager {
    
    WechatService getService(String appid);

    WechatService setService(String appId, WxMpMessageHandler msgHandler);

    WechatService setService(String appId, WxMpMessageHandler msgHandler, String scope);
}
