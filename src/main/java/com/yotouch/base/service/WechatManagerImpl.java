package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class WechatManagerImpl implements WechatManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WechatManagerImpl.class);
    
    @Autowired
    private YotouchApplication ytApp;
    
    private Map<String, WechatServiceImpl> wechatMap;
    
    @PostConstruct
    private void init() {
        this.wechatMap = new HashMap<>();
    }

    @Override
    public WechatServiceImpl getService(String appid) {
        logger.info("Wechat AppId " + appid);
        WechatServiceImpl wechatService = this.wechatMap.get(appid);
        return wechatService;
    }

    @Override
    public WechatServiceImpl setService(String appId, WxMpMessageHandler msgHandler) {
        DbSession dbSession = this.ytApp.getRuntime().createDbSession();
        Entity wechat = dbSession.queryOneRawSql("wechat", "appId = ?", new Object[]{appId});

        WechatServiceImpl wechatService = new WechatServiceImpl(ytApp, wechat);

        if (msgHandler != null) {
            wechatService.setMessageHandler(msgHandler);
        }

        this.wechatMap.put(appId, wechatService);

        return wechatService;
    }

}
