package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
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
    
    private Map<String, WeChatServiceImpl> wechatMap;
    
    @PostConstruct
    private void init() {
        this.wechatMap = new HashMap<>();
    }

    @Override
    public WeChatServiceImpl getService(String appid) {
        
        logger.info("Wechat AppId " + appid);
        
        WeChatServiceImpl wechatService = this.wechatMap.get(appid);
        if (wechatService != null) {
            return wechatService;
        }
        
        DbSession dbSession = this.ytApp.getRuntime().createDbSession();
        Entity wechat = dbSession.queryOneRawSql("wechat", "appId = ?", new Object[]{appid});
        
        wechatService = new WeChatServiceImpl(ytApp, wechat);
        
        this.wechatMap.put(appid, wechatService);
        
        return wechatService;
    }

}
