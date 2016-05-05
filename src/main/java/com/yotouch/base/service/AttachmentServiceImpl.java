package com.yotouch.base.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    protected YotouchApplication ytApp;

    public Entity saveAttachment(InputStream inputStream) {

        DbSession dbSession = ytApp.getRuntime().createDbSession();

        Entity att = null;

        try {
            String md5 = DigestUtils.md5DigestAsHex(inputStream);

            att = dbSession.queryOneRawSql("attachment", "md5 = ?", new Object[]{md5});

            if (att == null) {

                Tika tika = new Tika();
                String mime = tika.detect(inputStream);

                att = dbSession.newEntity("attachment");
                att.setValue("md5",  md5);
                att.setValue("content", inputStream);
                att.setValue("mime", mime);
                att = dbSession.save(att);
            }

        } catch (IOException e) {
            e.getMessage();

        }

        return att;

    }
}
