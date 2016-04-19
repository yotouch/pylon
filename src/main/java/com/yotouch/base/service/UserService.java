package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;

public interface UserService {

    String genPassword(Entity user, String password);
    
    String genLoginToken(Entity user);

    Entity checkLoginUser(String userToken);

    Entity modifyPassword(Entity currentuser, String password, String newPassword);

}
