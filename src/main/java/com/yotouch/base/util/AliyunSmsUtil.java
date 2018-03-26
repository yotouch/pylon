package com.yotouch.base.util;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 17/6/7.
 * 短信API产品的DEMO程序,工程中包含了一个SmsDemo类，直接通过
 * 执行main函数即可体验短信产品API功能(只需要将AK替换成开通了云通信-短信产品功能的AK即可)
 * 工程依赖了2个jar包(存放在工程的libs目录下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dysmsapi.jar
 *
 * 备注:Demo工程编码采用UTF-8
 * 国际短信发送请勿参照此DEMO
 */
@Component
public class AliyunSmsUtil {

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    @Value("${sms.aliyun.accessKey:}")
    private String accessKeyId ;
    @Value("${sms.aliyun.secretKey}")
    private String accessKeySecret ;

    /**
     * 发送短信
     *
     * @param signName - 必选，短信签名
     * @param templateCode - 必选，短信模板ID
     * @param phone - 必选，手机号码
     * @param params - 可选
     * @return
     * @throws ClientException
     */
    public SendSmsResponse sendSms(String signName, String templateCode, String phone, Map<String, String> params) throws ClientException {
        if(params == null){
            params = new HashMap<>() ;
        }

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000") ;
        System.setProperty("sun.net.client.defaultReadTimeout", "10000") ;

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret) ;
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain) ;
        IAcsClient acsClient = new DefaultAcsClient(profile) ;

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest() ;
        //必填:待发送手机号
        request.setPhoneNumbers(phone) ;
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName) ;
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode) ;
        //可选:模板中的变量替换JSON串
        request.setTemplateParam(JSON.toJSONString(params)) ;
        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request) ;

        return sendSmsResponse ;
    }


    /**
     * 查询发送记录
     *
     * @param phone - 必填
     * @param bizId - 可选，为空则查询改电话号码所有记录
     * @param sendTime - 可选，默认为当天
     * @param currentPage - 可选，默认 1
     * @param pageSize - 可选，默认 10
     * @return
     * @throws ClientException
     */
    private QuerySendDetailsResponse querySendDetails(String phone, String bizId, Date sendTime, long currentPage, long pageSize) throws ClientException {
        if(sendTime == null){
            sendTime = new Date() ;
        }
        if(pageSize <= 0){
            pageSize = 10 ;
        }
        if(currentPage <= 0){
            currentPage = 1 ;
        }

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000") ;
        System.setProperty("sun.net.client.defaultReadTimeout", "10000") ;

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret) ;
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain) ;
        IAcsClient acsClient = new DefaultAcsClient(profile) ;

        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest() ;
        //必填-号码
        request.setPhoneNumber(phone) ;
        //可选-流水号
        if(!StringUtils.isEmpty(bizId)){
            request.setBizId(bizId) ;
        }
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd") ;
        request.setSendDate(ft.format(sendTime)) ;
        //必填-页大小
        request.setPageSize(pageSize) ;
        //必填-当前页码从1开始计数
        request.setCurrentPage(currentPage) ;

        //hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request) ;

        return querySendDetailsResponse ;
    }
}
