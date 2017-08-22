package com.yotouch.base.web.controller;

import javax.servlet.http.HttpServletRequest;

import com.yotouch.core.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.yotouch.base.util.WebUtil;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.base.service.AttachmentService;
import com.yotouch.base.util.PropUtil;
import com.yotouch.core.entity.Entity;
import org.springframework.util.StringUtils;

public abstract class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    
    @Autowired
    protected YotouchApplication ytApp;

    @Autowired
    protected WebUtil webUtil;

    @Autowired
    protected PropUtil propUtil;

    @Autowired
    protected AttachmentService attService;


    @Autowired
    private DbSession dbSession;

    protected DbSession getDbSession() {
        return dbSession;
    }

    protected DbSession getDbSession(HttpServletRequest request) {
        DbSession dbSession = this.getDbSession();

        Entity loginUser = (Entity) request.getAttribute(Consts.RUNTIME_VARIABLE_USER);
        dbSession.setLoginUser(loginUser);
        return dbSession;
    }

    protected Entity doEditEntity(DbSession dbSession, String entityName, String uuid, HttpServletRequest request) {
        Entity entity = null;
        if (StringUtils.isEmpty(uuid)) {
            entity = dbSession.newEntity(entityName);
        } else {
            entity = dbSession.getEntity(entityName, uuid);
        }

        entity = webUtil.updateEntityVariables(entity, request);
        entity = dbSession.save(entity);
        return entity;
    }


}
