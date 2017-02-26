package com.yotouch.base.web.interceptor;

import com.yotouch.base.service.MenuPermissionChecker;
import com.yotouch.base.service.RoleService;
import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
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
public class AuthorizeInterceptor implements HandlerInterceptor {

    static final private Logger logger = LoggerFactory.getLogger(AuthorizeInterceptor.class);
    
    @Autowired
    private DbSession dbSession;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired(required = false)
    private MenuPermissionChecker menuPermissionChecker;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String uri = request.getRequestURI();

        Entity loginUser = (Entity) request.getAttribute(Consts.RUNTIME_VARIABLE_USER);

        if (loginUser != null) {
            List<Entity> userRoles = roleService.getUserRoles(loginUser);
            List<Entity> menus = roleService.getMenu(userRoles);
            
            if (menuPermissionChecker != null) {
                menus = menuPermissionChecker.check(request, dbSession, loginUser, menus);
            }
            
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

