package com.yotouch.base.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.yotouch.core.entity.Entity;

public interface AttachmentService {

    Entity saveAttachment(byte[] content, String contentType);

    Entity saveAttachment(byte[] content, String contentType, boolean md5Only);

    Entity saveAttachment(byte[] bytes);

    Entity saveAttachment(byte[] bytes, boolean md5Only);
}

