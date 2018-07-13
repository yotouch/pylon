package com.yotouch.base.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yotouch.base.util.QiniuUtil;
import com.yotouch.base.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yotouch.core.Consts;
import com.yotouch.core.runtime.YotouchApplication;

import java.io.IOException;
import java.util.UUID;

@Component
public class SystemInterceptor extends LoginInterceptor{
    
    @Autowired
    private YotouchApplication ytApp;

    @Value("${wechat.appId:}")
    private String wechatAppId;

    @Autowired
    private WebUtil webUtil;
    
    @Autowired
    private QiniuUtil qiniuUtil;

    public boolean addVariables(HttpServletRequest request, HttpServletResponse response, Object handler) {
        
        request.setAttribute(Consts.RUNTIME_VARIABLE_WX_APPID, wechatAppId);
        
        request.setAttribute("dbSession", ytApp.getRuntime().createDbSession());
        request.setAttribute("entityMgr", ytApp.getEntityManager());
        request.setAttribute("entityManager", ytApp.getEntityManager());
        request.setAttribute("request", request);
        request.setAttribute("webUtil", webUtil);
        request.setAttribute("qiniuUtil", qiniuUtil);
        request.setAttribute(Consts.RUNTIME_VARIABLE_BASE_URL, webUtil.getAppHostUrl());

        String bid = webUtil.getBrowserId(request);
        if (StringUtils.isEmpty(bid)) {
            bid = UUID.randomUUID().toString();
            Cookie c = new Cookie("_bid_", bid);
            c.setPath(webUtil.getDefaultCookiePath());
            c.setMaxAge(Integer.MAX_VALUE);
            response.addCookie(c);
        }
        
        request.setAttribute("_bid_", bid);
        return true;
    }

    @Override
    protected boolean loginSuccess(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        this.addVariables(request, response, handler);
        return true;
    }

    @Override
    protected boolean loginFailed(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        this.addVariables(request, response, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        
    }

}
