package com.yotouch.base.util;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 *  * 片云短信
 *   *
 *    * Created by leoliu on 2017/9/20.
 *     */
@Component
public class PianYunSmsUtil {

    @Value("${sms.pianyun.apiKey:}")
    private String apiKey ;

    private static final Logger logger = LoggerFactory.getLogger(PianYunSmsUtil.class) ;


    public void validCode(String phone, String code){
        String text = "您的验证码是 " + code ;
        sendSms(phone, text) ;
    }

    protected void sendSms(String phone, String text){

        if(StringUtils.isEmpty(apiKey)){
            logger.info("send sms by pianyun failed : not found api key") ;
            return ;
        }

        text = "【朱李叶】" + text ;
        YunpianClient clnt = new YunpianClient(apiKey).init() ;
        Map<String, String> param = clnt.newParam(2) ;
        param.put(YunpianClient.MOBILE, phone) ;
        param.put(YunpianClient.TEXT, text) ;
        Result<SmsSingleSend> r = clnt.sms().single_send(param) ;
        logger.info(" sms send code = " + r.getCode() + " msg=" + r.getMsg());
        clnt.close() ;

    }

}

