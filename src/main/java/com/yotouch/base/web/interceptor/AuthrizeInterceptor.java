package com.yotouch.base.web.interceptor;

import com.yotouch.base.service.RoleService;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.YotouchApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class AuthrizeInterceptor implements HandlerInterceptor {

    static final private Logger logger = LoggerFactory.getLogger(AuthrizeInterceptor.class);

    @Autowired
    private YotouchApplication ytApp;

    @Autowired
    private RoleService roleService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String uri = request.getRequestURI();

        Entity loginUser = (Entity) request.getAttribute("loginUser");

        if (loginUser != null) {
            List<Entity> userRoles = roleService.getUserRoles(loginUser);
            List<Entity> menus = roleService.getMenu(userRoles);
            request.setAttribute("userMenus", menus);
        }

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

