package com.yotouch.base.service;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class WechatMsgHandler implements WxMpMessageHandler {
    
    static final private Logger logger = LoggerFactory.getLogger(WechatMsgHandler.class);

    private WechatService wechatService;

    public WechatMsgHandler(WechatService wechatService) {
        this.wechatService = wechatService;
    }
    
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage inMsg, Map<String, Object> context, WxMpService wxMpService,
            WxSessionManager sessionManager) throws WxErrorException {
        
        logger.info("WECHAT get message " + context);
        logger.info("WECHAT get message " + inMsg);
        
        String replyText = "默认的回复";
        
        if (inMsg.getMsgType().equals(WxConsts.MASS_MSG_TEXT)) {
            
            String content = inMsg.getContent();
            if ("auth".equals(content)) {
                String state = "S-" + (new Date()).getTime();
                replyText = this.wechatService.genAuthUrl("", state);
            }
            
        }

        WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content(replyText).fromUser(inMsg.getToUserName())
                .toUser(inMsg.getFromUserName()).build();
        
        return m;
    }


}

/*
 *     @PostConstruct
    void init() {
        this.wechatService.setMessageHandler(new MsgHandler(this.wechatService));
    }

*/