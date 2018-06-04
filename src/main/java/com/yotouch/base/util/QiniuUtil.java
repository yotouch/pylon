package com.yotouch.base.util;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yotouch.base.service.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

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

    @Value("${qiniu.private:}")
    private String privateStr;
    
    @Autowired
    private AttachmentService attachmentService;

    private Auth auth;

    private UploadManager uploadManager;

    private OperationManager operationManager;
    
    @PostConstruct
    void init() {

        logger.info("Try to build auth class " + this.accessKey + " : " + this.secretKey);
        
        if (!StringUtils.isEmpty(this.accessKey)
                && !(StringUtils.isEmpty(this.secretKey))) {
            auth = Auth.create(this.accessKey, this.secretKey);
            uploadManager = new UploadManager();
            operationManager = new OperationManager(auth);
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
                e1.printStackTrace();
                logger.error(e1.getMessage(), e1);
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
        return getQiniuUrl(att, 60) ;
    }

    public String getQiniuUrl(Entity att, int expires){
        if (att == null) {
            return "";
        }

        String qiniuUrl = att.v("qiniuUrl");
        if (StringUtils.isEmpty(qiniuUrl)) {
            qiniuUrl  = "/attachment/" + att.getUuid();
        }

        String url = "http://" + this.domain + "/" + qiniuUrl;

        if ("1".equalsIgnoreCase(privateStr) || "true".equalsIgnoreCase(privateStr)) {
            expires = expires > 0 ? expires : 60 ;
            url = this.auth.privateDownloadUrl(url, expires) ;
        }

        return url;
    }

    public Entity getAndUploadQiniuUrlIgnoreSaveDb(DbSession dbSession, byte[] content) throws IOException {
        String md5 = DigestUtils.md5DigestAsHex(content);

        Entity att = dbSession.queryOneByField("attachment", "md5", md5);
        if (att != null) {
            return att;
        }

        String qiniuUrl = null;
        if (!StringUtils.isEmpty(qiniuUrl)) {
            return att;
        }
        
        att = attachmentService.saveAttachment(content, true);
        qiniuUrl = this.upload("attachment/" + att.getUuid(), content);
        att.setValue("qiniuUrl", qiniuUrl);
        att = dbSession.save(att);
        
        return att;
    }
    
    public String getAndUploadQiniuUrl(DbSession dbSession, Entity att) throws IOException {
        return this.getAndUploadQiniuUrl(dbSession, att, null);
    }


    public String getAndUploadQiniuUrl(DbSession dbSession, Entity att, StringMap params) throws IOException {
        if (att == null) {
            return "";
        }

        String qiniuUrl = att.v("qiniuUrl");
        if (StringUtils.isEmpty(qiniuUrl) || qiniuUrl.contains("error")) {
            qiniuUrl = this.upload(att, params);

            att.setValue("qiniuUrl", qiniuUrl);
            att = dbSession.save(att);
        }

        return "http://" + this.domain + "/" + qiniuUrl;
    }

    public String convertToMp3(byte[] content, String host) throws IOException {
        return convertToType(content, host, "avthumb/mp3");
    }

    public String convertToMp3(byte[] content, String host, String notiUrl) throws IOException {
        return convertToType(content, host, notiUrl, "avthumb/mp3");
    }

    public String convertToMp4(byte[] content, String host) throws IOException {
        return convertToType(content, host, "avthumb/mp4");
    }

    public String convertToType(byte[] content, String host, String url, String type) throws IOException {
        String name =  "attachment/" + System.currentTimeMillis() + "_" + Math.random() * 100;
        StringMap policy = new StringMap();
        policy.put("persistentOps",type);
        policy.put("persistentPipeline", this.pipeline);
        policy.put("persistentNotifyUrl", host + url);

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

    public String convertToType(byte[] content, String host, String type) throws IOException {
        return convertToType(content, host, persistentNotifyUrl, type);
    }


    public String getQiniuUrl(byte[] fileContent) {
        String name =  "attachment/" + System.currentTimeMillis() + "_" + Math.random() * 100;

        String qiniuUrl = "";
        try {
            qiniuUrl = this.upload(name, fileContent);
            return "http://" + this.domain + "/" + qiniuUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return qiniuUrl;

    }
    
    @Deprecated
    public String audioConcat(List<String> audioList, String name) {

        List<String> taList = new ArrayList<>();
        
        String key = audioList.get(0);
        boolean isFirst = true;
        for (String as: audioList) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            
            String s = "http://" + domain + "/" + as;
            s = UrlSafeBase64.encodeToString(s);
            taList.add(s);
        }

        String fops = "avconcat/2/format/mp3/" + Joiner.on("/").join(taList);

        String targetFile = "audiojoin/" + name + ".mp3";
        String saveas = UrlSafeBase64.encodeToString(bucket + ":" + targetFile);
        String pfops = fops + "|saveas/" + saveas;

        StringMap params = new StringMap()
                .putNotEmpty("pipeline", pipeline)
                .putNotEmpty("notifyURL", persistentNotifyUrl)
                ;

        try {
            String persistentId = operationManager.pfop(bucket, key, pfops, params);
            System.out.println(persistentId);
            return persistentId;
        } catch (QiniuException e) {
            e.printStackTrace();
            return "";
        }

    }

    public Entity saveAttachmentAndUpload(DbSession dbSession, byte[] bytes) throws IOException {
        Entity att = attachmentService.saveAttachment(bytes);
        if (att != null) {
            String qiniuUrl = att.v("qiniuUrl");
            if (StringUtils.isEmpty(qiniuUrl)) {
                String name = "attachment/" + att.getUuid();
                qiniuUrl = this.upload(name, bytes);
                att.setValue("qiniuUrl", qiniuUrl);
                att = dbSession.save(att);
            }
            return att;
        }
        return null;
    }

    public Map<String, Object> getMediaInfo(String qiniuUrl) throws IOException {

        URL url = new URL(qiniuUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // By default it is GET request
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        // Reading response from input Stream
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();


        Map<String, Object> tempPostResult = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        tempPostResult = mapper.readValue(response.toString(), new TypeReference<HashMap<String, Object>>() {});

        return tempPostResult;
    }
    
    


    public int getMediaDuration(String qiniuUrl){
        int duration = 0;

        try {
            Map<String, Object> result = getMediaInfo(qiniuUrl);
            Map postResult = (Map) result.get("format");
            if (postResult.get("duration") != null) {
                String durationString = (String) postResult.get("duration");
                double durationDouble = Double.parseDouble(durationString);
                duration = (int) durationDouble;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return duration;
    }
}
