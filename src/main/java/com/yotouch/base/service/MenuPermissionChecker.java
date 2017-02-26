package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface MenuPermissionChecker {
    
    List<Entity> check(HttpServletRequest request, DbSession dbSession, Entity user, List<Entity> menus);
    
}
