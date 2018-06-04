package com.yotouch.base.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yotouch.base.util.QiniuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.exception.Four04NotFoundException;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.base.service.AttachmentService;

@Controller
public class AttachmentController extends BaseController {
    
    static final private Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    
    @Autowired
    private YotouchApplication ytApp;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private QiniuUtil qnUtil;

    @Value("${attachment.cdn:}")
    String attachmentCnd;

    @Value("${attachment.saveDb:}")
    Boolean saveDb;

    @RequestMapping("/admin/attachment/test")
    public String testAttachment() {
        logger.info("Show attachment page");
        return "admin/testAttachment";
    }

    @RequestMapping("/admin/attachment/watermark")
    public @ResponseBody Map<String, Object> watermarkUpload(
            @RequestParam("attUuid") String attUuid,
            @RequestParam("watermarkUuid") String wmUuid,
            HttpServletRequest request
    ) {
        DbSession dbSession = this.getDbSession(request);
        Entity att = dbSession.getEntity("attachment", attUuid);
        byte[] attConetnt = att.v("content");

        Entity wm = dbSession.getEntity("attachment", attUuid);
        byte[] wmContent = wm.v("content");


        return null;

    }
    
    @RequestMapping("/admin/attachment/upload")
    public @ResponseBody Map<String, Object> uploadFile(
            @RequestParam("file") MultipartFile uploadfile,
            HttpServletRequest request
            ) throws IOException {
        
        String filename = uploadfile.getOriginalFilename();
        
        logger.info("Upload filename " + filename);
        Map<String, Object> ret = new HashMap<>();


        Entity attachment = null;
        if ("qiniu".equalsIgnoreCase(attachmentCnd)) {
            if (saveDb != null && !saveDb) {
                attachment = qnUtil.getAndUploadQiniuUrlIgnoreSaveDb(
                        getDbSession(request),
                        uploadfile.getBytes()
                );
            }
        }

        if (attachment == null) {
            attachment = attachmentService.saveAttachment(
                    uploadfile.getBytes(),
                    uploadfile.getContentType()
            );
        }

        ret.put("uuid", attachment.getUuid());
        return ret;
    }
    
    @RequestMapping("/attachment/get")
    public void getAttachment(
            @RequestParam(value="uuid") String uuid,
            HttpServletResponse resp
            ) throws IOException {


        showAttachment(uuid, resp);
    }

    @RequestMapping("/attachment/{uuid}")
    public void getRestAttachment(
            @PathVariable(value="uuid") String uuid,
            HttpServletResponse resp
    ) throws IOException {
        showAttachment(uuid, resp);
    }

    private void showAttachment(@RequestParam(value = "uuid") String uuid, HttpServletResponse resp) throws IOException {
        DbSession dbSession = ytApp.getRuntime().createDbSession();
        Entity att = dbSession.getEntity("attachment", uuid);
        if (att == null) {
            throw new Four04NotFoundException();
        }

        String qiniuUrl = att.v("qiniuUrl");
        if (!StringUtils.isEmpty(qiniuUrl)) {
            resp.sendRedirect(qnUtil.getQiniuUrl(att));
            return;
        }

        resp.reset();
        resp.setContentType(att.getValue("mime"));
        byte[] content = att.getValue("content");
        resp.setContentLength(content.length);

        FileCopyUtils.copy(content, resp.getOutputStream());
        resp.flushBuffer();
    }

    @RequestMapping("/attachment/qnurl/{uuid}")
    public @ResponseBody  String qiniuUrl(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request
    ) throws IOException {

        DbSession dbSession = this.getDbSession(request);
        Entity att = dbSession.getEntity("attachment", uuid);

        return qnUtil.getAndUploadQiniuUrl(dbSession, att);

    }
}
