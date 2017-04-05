package com.yotouch.base.service;

import org.apache.tika.Tika;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    @Autowired
    protected YotouchApplication ytApp;

    @Value("${app.attachment.saveBinary:}")
    String saveBinary;


    @Override
    public Entity saveAttachment(byte[] bytes) {
        Tika tika = new Tika();
        String mime = tika.detect(bytes);
        return this.saveAttachment(bytes, mime);
    }

    @Override
    public Entity saveAttachment(byte[] bytes, String contentType) {
        DbSession dbSession = ytApp.getRuntime().createDbSession();

        String md5 = DigestUtils.md5DigestAsHex(bytes);
        logger.info("Try to save attachment MD5 " + md5 + " size " + bytes.length);

        Entity att = dbSession.queryOneRawSql("attachment", "md5 = ?", new Object[]{md5});
        
        if (att == null) {
            att = dbSession.newEntity("attachment");
            att.setValue("md5",  md5);

            if (!"false".equalsIgnoreCase(saveBinary)) {
                att.setValue("content", bytes);
            }
            att.setValue("mime", contentType);
            att = dbSession.save(att);   
        }

        return att;
    }
}
