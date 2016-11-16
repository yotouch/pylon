package com.yotouch.base.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.base.wechat.ContextInterceptor;

import weixin.popular.api.MessageAPI;
import weixin.popular.api.TicketAPI;
import weixin.popular.api.TokenAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.message.message.NewsMessage;
import weixin.popular.bean.ticket.Ticket;
import weixin.popular.bean.token.Token;

public class WechatService {

    static final private Logger logger = LoggerFactory.getLogger(WechatService.class);

    private YotouchApplication ytApp;
    private WxMpInMemoryConfigStorage mpConfig;
    private WxMpService mpService;
    private WxMpMessageRouter wxMpMessageRouter;
    private String appId;

    private String oauth2Scope;
    public WechatService(YotouchApplication ytApp, String appId) {
        this(ytApp, appId, "snsapi_userinfo");
    }


    public WechatService(YotouchApplication ytApp, String appId, String oauth2Scope) {
        this.appId = appId;
        this.ytApp = ytApp;
        this.oauth2Scope = oauth2Scope;
        //this.dbSession = dbSession;

        DbSession dbSession = this.ytApp.getRuntime().createDbSession();
        Entity wechat = dbSession.queryOneRawSql("wechat", "appId = ?", new Object[]{appId});
        mpConfig = new WxMpInMemoryConfigStorage();
        mpConfig.setAppId(wechat.v("appId"));   // 设置微信公众号的appid
        mpConfig.setSecret(wechat.v("secret")); // 设置微信公众号的app corpSecret
        mpConfig.setToken(wechat.v("token"));   // 设置微信公众号的token
        mpConfig.setAesKey(wechat.v("aeskey")); // 设置微信公众号的EncodingAESKey

        mpService = new WxMpServiceImpl();
        mpService.setWxMpConfigStorage(mpConfig);
        wxMpMessageRouter = new WxMpMessageRouter(mpService);
    }

    @Deprecated
    public Entity getWechatEntity() {
        DbSession dbSession = this.ytApp.getRuntime().createDbSession();
        Entity wechat = dbSession.queryOneRawSql("wechat", "appId = ?", new Object[]{this.appId});
        return wechat;
    }

    public String genAuthUrl(String url, String state) {
        
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        
        //String myHost = (String) ytApp.getProp("host");
        Entity wechat = this.getWechatEntity();
        String domain = wechat.v("oauthDomain");
        String fullUrl = "http://" + domain + "/connect/wechat/"+this.appId+"/oauthCallback?url=" + url;

        /*
        try {
            fullUrl = URLEncoder.encode(fullUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */
        
        logger.info("Gen wechat auth url " + fullUrl);

        String redirectUrl = this.mpService.oauth2buildAuthorizationUrl(fullUrl, oauth2Scope, state);

        logger.info("Gen wechat redirect auth url " + redirectUrl);
        return redirectUrl;

    }

    public void setMessageHandler(WxMpMessageHandler msgHandler, String appHost) {
        wxMpMessageRouter.rule().interceptor(
                new ContextInterceptor(this.ytApp, this.appId, appHost, this)
        ).async(false).handler(msgHandler).end();
    }

    public boolean checkSignature(String timestamp, String nonce, String signature) {
        return this.mpService.checkSignature(timestamp, nonce, signature);
    }

    public WxMpConfigStorage getWechatConfig() {
        return mpConfig;
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

    public File generateTempQrcode(int id) throws WxErrorException {
        WxMpQrcodeService qrService = this.mpService.getQrcodeService();
        WxMpQrCodeTicket ticket = qrService.qrCodeCreateTmpTicket(id, 2592000);
        File qrcodeFile = qrService.qrCodePicture(ticket);
        return qrcodeFile;
    }

    public File generateQrcode(int id) throws WxErrorException {
        WxMpQrcodeService qrService = this.mpService.getQrcodeService();
        WxMpQrCodeTicket ticket = qrService.qrCodeCreateLastTicket(id);
        File codeFile = qrService.qrCodePicture(ticket);
        return codeFile;
    }

    public void createMenu(List<WxMenu.WxMenuButton> buttons) throws WxErrorException {
        WxMenu menu = new WxMenu();
        menu.setButtons(buttons);
        WxMpMenuService menuService = this.mpService.getMenuService();
        menuService.menuCreate(menu);
    }

    public Entity fillWechatUser(WxMpUser wxUser, Entity user) {
        user.setValue("status", Consts.STATUS_NORMAL);
        user.setValue("openId", wxUser.getOpenId());
        user.setValue("city", wxUser.getCity());
        user.setValue("country", wxUser.getCountry());
        user.setValue("headImgUrl", wxUser.getHeadImgUrl());
        user.setValue("language", wxUser.getLanguage());
        user.setValue("nickname", wxUser.getNickname());
        user.setValue("province", wxUser.getProvince());
        user.setValue("gender", wxUser.getSexId());
        logger.info("Subscribed " + wxUser.getSubscribe());
        user.setValue("subscribed", wxUser.getSubscribe());
        Long l = wxUser.getSubscribeTime();
        if (l != null) {
            user.setValue("subscribeTime", new Date(wxUser.getSubscribeTime() * 1000));
        }

        user.setValue("unionId", wxUser.getUnionId());

        return user;
    }

    public void sendTextMessage(String openId, String content) throws WxErrorException {
        WxMpCustomMessage msg = WxMpCustomMessage.TEXT().toUser(openId).content(content).build();
        this.mpService.customMessageSend(msg);
    }

    public String sendNews(String openId, String title, String desc, String url, String picUrl) {

        Entity wechat = this.getWechatEntity();

        NewsMessage.Article article = new NewsMessage.Article(title, desc, url, picUrl);
        NewsMessage nm = new NewsMessage(openId, Arrays.asList(article));
        BaseResult ret = MessageAPI.messageCustomSend(wechat.v("accessToken"), nm);

        if (ret.getErrcode().equals("40001")) {
            wechat = this.refreshAccessToken();
            ret = MessageAPI.messageCustomSend(wechat.v("accessToken"), nm);
        }

        return ret.getErrcode();
    }

    public void sendTemplateMessage(WxMpTemplateMessage tplMsg) throws WxErrorException {
        this.mpService.templateSend(tplMsg);
    }

    public Entity refreshAccessToken() {
        DbSession dbSession = this.ytApp.getRuntime().createDbSession();
        Entity wechat = dbSession.queryOneRawSql("wechat", "appId = ?", new Object[]{appId});

        this.refreshToken(wechat);
        this.refreshJsTicket(wechat);

        return dbSession.save(wechat);
    }

    public void checkAndRefreshAccessToken() {

        DbSession dbSession = this.ytApp.getRuntime().createDbSession();
        Entity wechat = dbSession.queryOneRawSql("wechat", "appId = ?", new Object[]{appId});

        boolean changed = false;
        Calendar tokenExpires = wechat.v("tokenExpires");
        if (tokenExpires == null || tokenExpires.getTime().getTime() <= System.currentTimeMillis()) {
            refreshToken(wechat);
            changed = true;
        }

        Calendar ticketExpires = wechat.v("ticketExpires");
        if (ticketExpires == null || ticketExpires.getTime().getTime() <= System.currentTimeMillis()) {
            refreshJsTicket(wechat);
            changed = true;
        }

        if (changed) {
            dbSession.save(wechat);
        }
    }

    private void refreshJsTicket(Entity wechat) {
        String accessToken = wechat.v("accessToken");
        logger.info("accessToken " + accessToken);
        Ticket ticket = TicketAPI.ticketGetticket(accessToken);
        logger.info("ticket " + ticket);
        wechat.setValue("jsapiTicket", ticket.getTicket());
        wechat.setValue("ticketExpires", new Date(System.currentTimeMillis() + 1000 * 60 * 15));
    }

    private void refreshToken(Entity wechat) {
        Token token = TokenAPI.token(wechat.v("appId"), wechat.v("secret"));
        logger.info("Get new token " + token);
        wechat.setValue("accessToken", token.getAccess_token());
        wechat.setValue("tokenExpires", new Date(System.currentTimeMillis() + 1000 * 60 * 15)); // 每15分钟刷新一次
    }
}
