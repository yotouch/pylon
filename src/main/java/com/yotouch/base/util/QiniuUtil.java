package com.yotouch.base.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${qiniu.domain}")
    private String domain;

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

    public String upload(String name, byte[] content) throws IOException {
        try {
            Response res = uploadManager.put(content, name, this.getOverwriteToken(name));

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

    public String upload(Entity att) throws IOException {
        return this.upload("attachment/" + att.getUuid(), att.v("content"));
    }

    public String getQiniuUrl(Entity att) {
        String qiniuUrl = att.v("qiniuUrl");
        if (StringUtils.isEmpty(qiniuUrl)) {
            return "/attachment/" + att.getUuid();
        }
        return qiniuUrl;
    }

    public String getQiniuUrl(DbSession dbSession, Entity att) throws IOException {
        String qiniuUrl = att.v("qiniuUrl");
        if (StringUtils.isEmpty(qiniuUrl)) {
            qiniuUrl = this.upload(att);

            att.setValue("qiniuUrl", qiniuUrl);
            att = dbSession.save(att);
        }

        return "http://" + this.domain + "/" + qiniuUrl;
    }


}
