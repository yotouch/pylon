package com.yotouch.base.service;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;

public interface WechatManager {
    
    WeChatServiceImpl getService(String appid);

    WeChatServiceImpl setService(String appId, WxMpMessageHandler msgHandler);

}
