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

    List<Entity> getUserList(DbSession dbSession, String roleName);

    //Entity getOrCreateByName(String parentRole, String name);

    Entity getOrCreateByName(String name);

    Entity getByName(String roleName);

    boolean hasRole(Entity user, Entity role);

    boolean hasRole(Entity user, String roleName);

    Entity getTopRole(DbSession dbSession, String topRoleName);

    List<Entity> getAllChildRoles(DbSession dbSession, Entity topRole);
    
    void addRole(DbSession dbSession, Entity user, String roleName);

    void addRole(DbSession dbSession, Entity user, Entity role);

    List<Entity> getUserList(DbSession dbSession, List<Entity> roles);
    
    List<Entity> getUserList(DbSession dbSession, Entity role);

    void removeUserRole(DbSession dbSession, Entity user, Entity role);

    void removeUserRole(DbSession dbSession, Entity user, String roleName);

}
