package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {

    String genPassword(Entity user, String password);
    
    String genLoginToken(Entity user);

    Entity checkLoginUser(String userToken);

    Entity modifyPassword(Entity currentuser, String password, String newPassword);

    void seedLoginCookie(HttpServletResponse response, Entity user);

    List<Entity> getUsersByRole(DbSession dbSession, Entity role, int status);

    @Deprecated
    List<Entity> getUserRoles(DbSession dbSession, List<Entity> roles);
    
    List<Entity> getUserRoleList(DbSession dbSession, Entity user);

    boolean checkPassword(DbSession dbSession, Entity user, String password);
}
