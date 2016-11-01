package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class WechatManagerImpl implements WechatManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WechatManagerImpl.class);
    
    @Autowired
    private YotouchApplication ytApp;
    
    @Value("${app.host:}")
    private String appHost;

    private Map<String, WechatService> wechatMap;

    @PostConstruct
    private void init() {
        this.wechatMap = new HashMap<>();
    }

    @Override
    public WechatService getService(String appid) {
        //logger.info("Wechat AppId " + appid);
        WechatService wechatService = this.wechatMap.get(appid);
        return wechatService;
    }

    @Override
    public WechatService setService(String appId, WxMpMessageHandler msgHandler) {
        return this.setService(appId, msgHandler, "snsapi_userinfo");
    }

    @Override
    public WechatService setService(String appId, WxMpMessageHandler msgHandler, String scope) {
        DbSession dbSession = this.ytApp.getRuntime().createDbSession();

        WechatService wechatService = new WechatService(ytApp, appId, scope);

        if (msgHandler != null) {
            wechatService.setMessageHandler(msgHandler, appHost);
        }

        this.wechatMap.put(appId, wechatService);

        return wechatService;
    }

}
