package com.yotouch.base.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Component
public class QiniuUtil {

    private static final Logger logger = LoggerFactory.getLogger(QiniuUtil.class);

    @Value("${qiniu.accessKey:}")
    private String accessKey;

    @Value("${qiniu.secretKey:}")
    private String secretKey;

    @Value("${qiniu.bucket:}")
    private String bucket;

    @Value("${qiniu.domain:}")
    private String domain;

    @Value("${qiniu.persistentNotifyUrl:}")
    private String persistentNotifyUrl;

    @Value("${qiniu.pipeline:}")
    private String pipeline;

    private Auth auth;

    private UploadManager uploadManager;

    @PostConstruct
    void init() {

        logger.info("Try to build auth class " + this.accessKey + " : " + this.secretKey);

        if (!StringUtils.isEmpty(this.accessKey)
                && !(StringUtils.isEmpty(this.secretKey))) {
            auth = Auth.create(this.accessKey, this.secretKey);
            uploadManager = new UploadManager();
        }


        logger.info("auth " + this.auth + " bucket " + this.bucket);

    }

    private String getOverwriteToken(String key) {
        return this.getOverwriteToken(this.bucket, key);
    }

    private String getOverwriteToken(String bucket, String key) {
        return auth.uploadToken(bucket, key);
    }

    private String getOverwriteToken(String bucket, String key, StringMap policy) {
        return auth.uploadToken(bucket, key, 3600, policy);
    }

    public String upload(String name, byte[] content, StringMap params) throws IOException {
        try {
            Response res = uploadManager.put(content, name, this.getOverwriteToken(name), params, null, false);

            // {"hash":"Fpi5b04B_7eMUiy_dyiRaz6elwYZ","key":"067046ba-4639-4a36-ad47-edd4882b7c1e"}

            JSONObject obj = JSON.parseObject(res.bodyString());

            return obj.getString("key");
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error(r.toString(), e);;
            try {
                //响应的文本信息
                return r.bodyString();
            } catch (QiniuException e1) {
                return "";
            }
        }
    }

    public String upload(String name, byte[] content) throws IOException {
        return this.upload(name, content, null);
    }

    public String upload(Entity att, StringMap params) throws IOException {
        return this.upload("attachment/" + att.getUuid(), att.v("content"), params);
    }

    public String upload(Entity att) throws IOException {
        return this.upload("attachment/" + att.getUuid(), att.v("content"), null);
    }

    public String getQiniuUrl(Entity att) {
        if (att == null) {
            return "";
        }

        String qiniuUrl = att.v("qiniuUrl");
        if (StringUtils.isEmpty(qiniuUrl)) {
            return "/attachment/" + att.getUuid();
        }
        return qiniuUrl;
    }

    public String getAndUploadQiniuUrl(DbSession dbSession, Entity att) throws IOException {
        return this.getAndUploadQiniuUrl(dbSession, att, null);
    }


    public String getAndUploadQiniuUrl(DbSession dbSession, Entity att, StringMap params) throws IOException {
        if (att == null) {
            return "";
        }

        String qiniuUrl = att.v("qiniuUrl");
        if (StringUtils.isEmpty(qiniuUrl)) {
            qiniuUrl = this.upload(att, params);

            att.setValue("qiniuUrl", qiniuUrl);
            att = dbSession.save(att);
        }

        return "http://" + this.domain + "/" + qiniuUrl;
    }

    public String convertToMp3(byte[] content, String host) throws IOException {

        String name =  "attachment/" + System.currentTimeMillis() + "_" + Math.random() * 100;
        StringMap policy = new StringMap();
        policy.put("persistentOps","avthumb/mp3");
        policy.put("persistentPipeline", this.pipeline);
        policy.put("persistentNotifyUrl", host + persistentNotifyUrl);

        try {
            Response res = uploadManager.put(content, name, getOverwriteToken(this.bucket, name, policy));

            // {"persistentId": <persistentId>}
            JSONObject obj = JSON.parseObject(res.bodyString());

            return obj.getString("persistentId");
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error(r.toString(), e);;
            try {
                //响应的文本信息
                return r.bodyString();
            } catch (QiniuException e1) {
                return "";
            }
        }
    }

}
