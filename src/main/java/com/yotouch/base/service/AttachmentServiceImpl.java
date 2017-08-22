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

    @Override
    public Entity saveAttachment(byte[] bytes) {
        return this.saveAttachment(bytes, false);
    }

    @Override
    public Entity saveAttachment(byte[] bytes, boolean md5Only) {
        Tika tika = new Tika();
        String mime = tika.detect(bytes);
        return this.saveAttachment(bytes, mime, md5Only);
    }

    @Override
    public Entity saveAttachment(byte[] bytes, String contentType) {
        return this.saveAttachment(bytes, contentType, false);
    }
    @Override
    public Entity saveAttachment(byte[] bytes, String contentType, boolean md5Only) {
        DbSession dbSession = ytApp.getRuntime().createDbSession();

        String md5 = DigestUtils.md5DigestAsHex(bytes);
        logger.info("Try to save attachment MD5 " + md5 + " size " + bytes.length);

        Entity att = dbSession.queryOneRawSql("attachment", "md5 = ?", new Object[]{md5});
        
        if (att == null) {
            att = dbSession.newEntity("attachment");
            att.setValue("md5",  md5);

            if (!md5Only) {
                att.setValue("content", bytes);
            }
            att.setValue("mime", contentType);
            att = dbSession.save(att);
            
        }

        return att;
    }
}
