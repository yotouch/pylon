package com.yotouch.base.web.controller;

import com.yotouch.base.service.AttachmentService;
import com.yotouch.base.service.WechatManager;
import com.yotouch.base.web.util.PropUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.yotouch.base.web.util.WebUtil;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;

public abstract class BaseController {
    
    @Autowired
    protected YotouchApplication ytApp;

    @Autowired
    protected WebUtil webUtil;

    @Autowired
    protected PropUtil propUtil;

    @Autowired
    protected WechatManager wechatMgr;

    @Autowired
    protected AttachmentService attService;

    protected DbSession getDbSession() {
        return this.ytApp.getRuntime().createDbSession();        
    }

}
