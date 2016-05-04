package com.yotouch.base.service;

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

    public Entity saveAttachment(MultipartFile uploadfile){

        DbSession dbSession = ytApp.getRuntime().createDbSession();

        Entity att = null;

        try {

            byte[] bytes = uploadfile.getBytes();

            String md5 = DigestUtils.md5DigestAsHex(bytes);

            att = dbSession.queryOneRawSql("attachment", "md5 = ?", new Object[]{md5});

            if (att == null) {

                Tika tika = new Tika();
                String mime = tika.detect(bytes);

                att = dbSession.newEntity("attachment");
                att.setValue("md5",  md5);
                att.setValue("content", uploadfile.getBytes());
                att.setValue("mime", mime);
                att = dbSession.save(att);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return att;

    }
}
