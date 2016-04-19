package com.yotouch.base.service;

import java.util.List;

import com.yotouch.core.entity.Entity;

public interface RoleService {
    
    List<Entity> getRole(Entity user);

    List<Entity> getMenu(List<Entity> userRoles);
    
    void saveUserRole(Entity user, String[] role);
    
    void deleteUserRoles(Entity user);
    
    List<Entity> getSubRoleList(String parentRoleUuid);

    List<Entity> getUserList(List<Entity> roles);

    Entity getOrCreateByName(String name);

    boolean hasRole(Entity user, Entity role);
}
