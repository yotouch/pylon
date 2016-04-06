package com.yotouch.base.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.yotouch.base.web.util.WebUtil;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;

public abstract class BaseController {
    
    @Autowired
    protected YotouchApplication ytApp;

    @Autowired
    protected WebUtil webUtil;

    
    protected DbSession getDbSession() {
        return this.ytApp.getRuntime().createDbSession();        
    }

}
