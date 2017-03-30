package com.yotouch.base.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yotouch.core.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.yotouch.base.service.UserService;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.YotouchApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public abstract class LoginInterceptor implements HandlerInterceptor {

    static final private Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private YotouchApplication ytApp;

    @Autowired
    private UserService userService;

    private List<String> ignoredList;

    public LoginInterceptor() {
        this(new ArrayList<>());
    }

    public LoginInterceptor(List<String> ignoredList) {
        this.ignoredList = new ArrayList<>(ignoredList);

        tryAdd("/login");
        tryAdd("/connect");

    }

    protected void tryAdd(String s) {
        if (this.ignoredList.contains(s)) {
            return;
        }

        this.ignoredList.add(s);
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        boolean isLogin = false;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("userToken".equalsIgnoreCase(c.getName())) {
                    String userToken = c.getValue();
                    //logger.info("Checking userToken " + userToken);

                    Entity user = userService.checkLoginUser(userToken);
                    if (user != null) {
                        request.setAttribute("loginUser", user);
                        request.setAttribute(Consts.RUNTIME_VARIABLE_USER, user);

                        // TODO add role menu
                        isLogin = true;
                    } else {
                        Cookie cookie = new Cookie("userToken", "");
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }
        }

        boolean isIgnore = this.uriIsIgnore(request) ;
        if(isIgnore){
            isLogin = true ;
        }

        if (isLogin) {
            return this.loginSuccess(request, response, handler);
        } else {
            return this.loginFailed(request, response, handler);
        }
    }

    /**
     * 检测当前 URI 是否不需要权限控制
     *
     * @param request
     * @return
     */
    protected boolean uriIsIgnore(HttpServletRequest request){
        String uri = request.getRequestURI();

        for (String iu: this.ignoredList) {
            if (uri.startsWith(iu)) {
                return true;
            }
        }

        return false ;
    }

    protected abstract boolean loginSuccess(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException;

    protected abstract boolean loginFailed(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

}

