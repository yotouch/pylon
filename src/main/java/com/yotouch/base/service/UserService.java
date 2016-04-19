package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

public interface UserService {

    String genPassword(Entity user, String password);
    
    String genLoginToken(Entity user);

    Entity checkLoginUser(String userToken);

    Entity modifyPassword(Entity currentuser, String password, String newPassword);

    Entity addDefaultRoleByEnterUrl(DbSession dbSession, Entity user, String enterUrl);;
    
}
