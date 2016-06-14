package com.yotouch.base.service;

import com.yotouch.base.wechat.ContextInterceptor;
import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.YotouchApplication;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.*;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class WechatService {

    static final private Logger logger = LoggerFactory.getLogger(WechatService.class);

    private YotouchApplication ytApp;
    private WxMpInMemoryConfigStorage mpConfig;
    private WxMpService mpService;
    private WxMpMessageRouter wxMpMessageRouter;
    
    private Entity wechat;
    
    public WechatService(YotouchApplication ytApp, Entity wechat) {
        this.ytApp = ytApp;
        this.wechat = wechat;

        mpConfig = new WxMpInMemoryConfigStorage();

        mpConfig.setAppId(wechat.v("appId"));   // 设置微信公众号的appid
        mpConfig.setSecret(wechat.v("secret")); // 设置微信公众号的app corpSecret
        mpConfig.setToken(wechat.v("token"));   // 设置微信公众号的token
        mpConfig.setAesKey(wechat.v("aeskey")); // 设置微信公众号的EncodingAESKey

        mpService = new WxMpServiceImpl();
        mpService.setWxMpConfigStorage(mpConfig);
        wxMpMessageRouter = new WxMpMessageRouter(mpService);
    }
    
    public Entity getWechatEntity() {
        return this.wechat;
    }

    public String genAuthUrl(String url, String state) {
        
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        
        //String myHost = (String) ytApp.getProp("host");
        String domain = this.wechat.v("oauthDomain");
        String fullUrl = "http://" + domain + "/connect/wechat/"+this.wechat.v("appId")+"/oauthCallback?url=" + url;

        /*
        try {
            fullUrl = URLEncoder.encode(fullUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */
        
        logger.info("Gen wechat auth url " + fullUrl);
        

        String redirectUrl = this.mpService.oauth2buildAuthorizationUrl(fullUrl, WxConsts.OAUTH2_SCOPE_USER_INFO,
                state);

        logger.info("Gen wechat redirect auth url " + redirectUrl);
        return redirectUrl;

    }

    public void setMessageHandler(WxMpMessageHandler msgHandler) {
        wxMpMessageRouter.rule().interceptor(
                new ContextInterceptor(this.ytApp, this.wechat.v("appId"), this)
        ).async(false).handler(msgHandler).end();
    }

    public boolean checkSignature(String timestamp, String nonce, String signature) {
        return this.mpService.checkSignature(timestamp, nonce, signature);
    }

    public WxMpConfigStorage getWechatConfig() {
        return this.mpConfig;
    }

    public WxMpOAuth2AccessToken oauth2getAccessToken(String code) throws WxErrorException {
        return this.mpService.oauth2getAccessToken(code);
    }

    public WxMpUser oauth2getUserInfo(WxMpOAuth2AccessToken accessToken) throws WxErrorException {
        return this.mpService.oauth2getUserInfo(accessToken, "");
    }

    public WxMpXmlOutMessage route(WxMpXmlMessage inMsg) {
        return this.wxMpMessageRouter.route(inMsg);
    }

    public WxJsapiSignature createJsapiSignature(String fullUrl) throws WxErrorException {
        WxJsapiSignature jss = this.mpService.createJsapiSignature(fullUrl);
        return jss;        
    }

    public File generateQrcode(int id) throws WxErrorException {
        WxMpQrCodeTicket ticket = this.mpService.qrCodeCreateLastTicket(id);
        File codeFile = this.mpService.qrCodePicture(ticket);
        return codeFile;
    }

    public void createMenu(List<WxMenu.WxMenuButton> buttons) throws WxErrorException {
        WxMenu menu = new WxMenu();
        menu.setButtons(buttons);
        this.mpService.menuCreate(menu);
    }

    public Entity fillWechatUser(WxMpUser wxUser, Entity user) {
        user.setValue("status", Consts.STATUS_NORMAL);
        user.setValue("openId", wxUser.getOpenId());
        user.setValue("city", wxUser.getCity());
        user.setValue("country", wxUser.getCountry());
        user.setValue("headImgUrl", wxUser.getHeadImgUrl());
        user.setValue("language", wxUser.getLanguage());
        if (StringUtils.isEmpty(user.getValue("nickname"))) {
            user.setValue("nickname", wxUser.getNickname());
        }
        user.setValue("province", wxUser.getProvince());
        user.setValue("gender", wxUser.getSexId());
        logger.info("Subscribed " + wxUser.getSubscribe());
        user.setValue("subscribed", wxUser.getSubscribe());
        Long l = wxUser.getSubscribeTime();
        if (l != null) {
            user.setValue("subscribeTime", new Date(wxUser.getSubscribeTime()));
        }

        user.setValue("unionId", wxUser.getUnionId());

        return user;
    }


}
