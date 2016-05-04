package com.yotouch.base.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.yotouch.core.entity.Entity;

public interface UploadFileService {

    Map<String, Object> saveAttachment(MultipartFile file);

}

