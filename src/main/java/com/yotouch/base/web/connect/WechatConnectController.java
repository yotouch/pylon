package com.yotouch.base.web.connect;

import com.yotouch.base.service.WeChatServiceImpl;
import com.yotouch.base.service.WechatManager;
import com.yotouch.base.web.BaseController;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
}