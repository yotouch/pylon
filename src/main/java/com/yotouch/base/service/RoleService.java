package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

import java.util.List;

public interface RoleService {
    
    List<Entity> getUserRoles(Entity user);

    List<Entity> getMenu(List<Entity> userRoles);
    
    void addUserRole(Entity user, String[] role);

    void deleteUserRoles(Entity user);
    
    List<Entity> getSubRoleList(String parentRoleUuid);

    List<Entity> getUserList(List<Entity> roles);

    Entity getOrCreateByName(String name);

    boolean hasRole(Entity user, Entity role);

    Entity getTopRole(DbSession dbSession, String topRoleName);

    List<Entity> getAllChildRoles(DbSession dbSession, Entity topRole);

}
