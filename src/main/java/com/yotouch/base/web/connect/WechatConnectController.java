package com.yotouch.base.web.connect;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import com.yotouch.base.service.WeChatServiceImpl;
import com.yotouch.base.service.WechatManager;
import com.yotouch.base.web.BaseController;
import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

@Controller
public class WechatConnectController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WechatConnectController.class);

    @Autowired
    private WechatManager wechatMgr;

    @Value("${wechat.appId}")
    private String defaultWechatAppId;

    private WeChatServiceImpl getWechatService(String wxUuid) {
        String wechatId = "";
        if (StringUtils.isEmpty(wxUuid)) {
            wechatId = defaultWechatAppId;
        } else {
            // TODO: 16/5/6 here should read wechat info from db
            wechatId = defaultWechatAppId;
        }

        WeChatServiceImpl wcService = this.wechatMgr.getService(wechatId);
        return wcService;
    }

    @RequestMapping(value = "/connect/wechat/{uuid}", method = RequestMethod.GET)
    public
    @ResponseBody
    String connect(
            @PathVariable("uuid") String wxUuid,
            @RequestParam(value = "signature", defaultValue = "") String signature,
            @RequestParam(value = "nonce", defaultValue = "") String nonce,
            @RequestParam(value = "timestamp", defaultValue = "") String timestamp,
            @RequestParam(value = "echostr", defaultValue = "") String echostr,
            HttpServletRequest request
    ) {
        WeChatServiceImpl wcService = getWechatService(wxUuid);

        if (wcService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "";
    }

    @RequestMapping(value = "/connect/wechat/{uuid}", method = RequestMethod.POST)
    public
    @ResponseBody
    String doConnect(
            @PathVariable("uuid") String uuid,
            @RequestParam(value = "signature", defaultValue = "") String signature,
            @RequestParam(value = "nonce", defaultValue = "") String nonce,
            @RequestParam(value = "timestamp", defaultValue = "") String timestamp,
            @RequestParam(value = "encrypt_type", defaultValue = "raw") String encryptType,
            @RequestParam(value = "msg_signature", defaultValue = "") String msgSig,
            @RequestBody String body,
            HttpServletRequest request) throws IOException {

        WeChatServiceImpl wcService = getWechatService(uuid);
        if (!wcService.checkSignature(timestamp, nonce, signature)) {
            logger.info("WECHAT WRONG");
            return "WRONG";
        }

        WxMpXmlMessage inMsg = null;
        if ("raw".equals(encryptType)) {
            inMsg = WxMpXmlMessage.fromXml(body);
        } else {
            inMsg = WxMpXmlMessage.fromEncryptedXml(body, wcService.getWechatConfig(), timestamp, nonce, msgSig);
        }

        if (WxConsts.XML_MSG_EVENT.equals(inMsg.getMsgType())) {
            logger.info("Event " + inMsg.getEvent() + " " + inMsg.getEventKey());
        }

        return "";
    }

    @RequestMapping("/connect/wechat/{uuid}/oauth")
    public String oauth(
            @PathVariable("uuid") String uuid,
            @RequestParam(value="state", defaultValue="") String state,
            @RequestParam(value="backUrl", defaultValue="") String backUrl,
            HttpServletRequest request
    ) {

        WeChatServiceImpl wcService = getWechatService(uuid);

        String fullUrl = webUtil.getBaseUrl(request) + "/connect/wechat/"+uuid+"/oauthCallback?state="+state+"&amp;url=" +backUrl;

        String authUrl = wcService.genAuthUrl(fullUrl, state);
        return "redirect:" + authUrl;
    }

    /*
     * http://app.taomovie.cn/connect/wechat/oauthCallback?
     *  state=S-1455352106593 &
     *  url=http://baidu.com &
     *  code=041ce3d9c65289aa8f7aa710fca64edY&state=S-1455352106593
     */
    @RequestMapping("/connect/wechat/{uuid}/oauthCallback")
    public String oauthCallback(
            @PathVariable("uuid") String uuid,
            @RequestParam(value="url", defaultValue = "") String url,
            @RequestParam(value="state", defaultValue = "") String state,
            @RequestParam(value="code", defaultValue = "") String code,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws WxErrorException {
        WeChatServiceImpl wcService = getWechatService(uuid);

        WxMpOAuth2AccessToken accessToken = wcService.oauth2getAccessToken(code);
        WxMpUser wxUser = wcService.oauth2getUserInfo(accessToken);

        DbSession dbSession = this.getDbSession();
        String openId = wxUser.getOpenId();
        Entity user = dbSession.queryOneRawSql("user", "wxOpenId = ?", new Object[]{openId});

        if (user == null) {
            user = dbSession.newEntity("user");
            user.setValue("wxOpenId", openId);
            user.setValue("status", Consts.STATUS_NORMAL);
        }

        user.setValue("city", wxUser.getCity());
        user.setValue("country", wxUser.getCountry());
        user.setValue("headImgUrl", wxUser.getHeadImgUrl());
        user.setValue("language", wxUser.getLanguage());
        if (StringUtils.isEmpty(user.getValue("nickname"))) {
            user.setValue("nickname", wxUser.getNickname());
        }
        user.setValue("province", wxUser.getProvince());
        user.setValue("gender", wxUser.getSexId());
        user.setValue("subscribe", wxUser.getSubscribe());
        Long l = wxUser.getSubscribeTime();
        if (l != null) {
            user.setValue("subscribeTime", new Date(wxUser.getSubscribeTime()));
        }

        user.setValue("wxUnionId", wxUser.getUnionId());
        user.setValue("wxAccessToken", accessToken.getAccessToken());
        user.setValue("wxAccessTokenExpires", new Date(accessToken.getExpiresIn() + System.currentTimeMillis()));
        user.setValue("wxRefreshToken", accessToken.getRefreshToken());

        dbSession.save(user);


        Cookie c = new Cookie(Consts.COOKIE_NAME_WX_USER_UUID, user.getUuid());
        c.setPath("/");
        response.addCookie(c);

        return "redirect:" + url;
    }
}