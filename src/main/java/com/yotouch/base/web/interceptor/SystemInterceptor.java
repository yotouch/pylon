package com.yotouch.base.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.yotouch.core.Consts;
import com.yotouch.core.runtime.YotouchApplication;

@Component
public class SystemInterceptor implements HandlerInterceptor{
    
    @Autowired
    private YotouchApplication ytApp;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        request.setAttribute(Consts.RUNTIME_VARIABLE_WX_APPID, (String) ytApp.getProp("wechatAppId"));
        
        request.setAttribute("dbSession", ytApp.getRuntime().createDbSession());
        request.setAttribute("entityMgr", ytApp.getEntityManager());
        request.setAttribute("request", request);

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
