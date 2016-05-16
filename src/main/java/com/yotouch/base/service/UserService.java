package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;

import javax.servlet.http.HttpServletResponse;

public interface UserService {

    String genPassword(Entity user, String password);
    
    String genLoginToken(Entity user);

    Entity checkLoginUser(String userToken);

    Entity modifyPassword(Entity currentuser, String password, String newPassword);

    void seedLoginCookie(HttpServletResponse response, Entity user);
}
