package com.yotouch.base.service;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;

public interface WechatManager {
    
    WechatServiceImpl getService(String appid);

    WechatServiceImpl setService(String appId, WxMpMessageHandler msgHandler);

}
