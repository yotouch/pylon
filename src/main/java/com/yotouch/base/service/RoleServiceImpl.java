package com.yotouch.base.service;

import com.yotouch.base.util.WebUtil;
import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.runtime.YotouchRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    protected WebUtil webUtil;

    @Autowired
    private DbSession dbSession;

    public List<Entity> getUserRoles(Entity user){

        List<Entity> userRoles = dbSession.queryRawSql("userRole", "userUuid=? AND status=?", new Object[]{user.v("uuid"), Consts.STATUS_NORMAL});

        return userRoles;
    }

    public List<Entity> getMenu(List<Entity> userRoles){

        List<Entity> menus = new ArrayList<Entity>();

        for(Entity r:userRoles){

            List<Entity> roleMenus = dbSession.queryRawSql("roleMenu", "roleUuid=? AND status=?", new Object[]{r.v("role"), Consts.STATUS_NORMAL});

            if (roleMenus != null){
                for (Entity m : roleMenus) {
                    Entity menu = dbSession.getEntity("menu", m.v("menu"));

                    if (menu != null) {
                        menus.add(menu);
                    }

                }
            }
        }


        menus = filterRepeatMenu(menus);
        menus = sortMenu(menus);

        return menus;
    }
    
    @Transactional
    public void addUserRole(Entity user, String[] roles){

        for (String roleUuid : roles) {
            Entity userRole = dbSession.newEntity("userRole", Consts.STATUS_NORMAL);
            userRole.setValue("role", roleUuid);
            userRole.setValue("user", user.getUuid());
            dbSession.save(userRole);

        }
        
    }

    @Transactional
    public void deleteUserRoles(Entity user){
        List<Entity> userRoles = dbSession.queryRawSql("userRole", "userUuid = ? AND status = ?", new Object[]{user.getUuid(), Consts.STATUS_NORMAL});
        
        for (Entity userRole : userRoles) {
            userRole.setValue("status", Consts.STATUS_DELETED);
            dbSession.save(userRole);
        }
    }

    protected List<Entity> sortMenu(List<Entity> menus){
        Collections.sort(menus, new Comparator<Entity>() {
            @Override
            public int compare(Entity m1, Entity m2) {
                int result = (Integer)(m2.v("weight")) - (Integer)(m1.v("weight"));
                return result;
            }
        });
        
        return menus;
    }

    protected List<Entity> filterRepeatMenu(List<Entity> menus){
        List<Entity> resultMenus = new ArrayList<Entity>(); 
        
        for(Entity menu : menus) {
            if(!resultMenus.contains(menu)){
                resultMenus.add(menu);
            }
        }
        
        return resultMenus;
    }
    
    
    
    public List<Entity> getSubRoleList(String parentRoleUuid){
        List<Entity> roles = dbSession.queryRawSql("role", "parentUuid = ? AND status = ?", new Object[]{parentRoleUuid, Consts.STATUS_NORMAL});
        return roles;
    }
    
    public List<Entity> getUserList(List<Entity> roles){
        HashSet<Entity> users = new HashSet<>();
        
        for(Entity role : roles){
            List<Entity> currentUserRoles = dbSession.queryRawSql("userRole", "roleUuid = ? AND status = ?", new Object[]{role.getUuid(), Consts.STATUS_NORMAL});
            
            for (Entity userRole : currentUserRoles) {
                Entity user = dbSession.getEntity("user", userRole.v("user"));
                users.add(user);
            }
        }
        
        List<Entity> resultUsers = new ArrayList<Entity>(users);
        return resultUsers;
    }

    @Override
    public Entity getOrCreateByName(String name) {

        Entity role = dbSession.queryOneRawSql("role", "name = ?", new Object[]{name});
        if (role == null) {
            role = dbSession.newEntity("role");
            role.setValue("name", name);
            role = dbSession.save(role);
        }

        return role;

    }

    @Override
    public boolean hasRole(Entity user, Entity role) {
        List<Entity> userRoles = this.getUserRoles(user);
        List<Entity> roles = new ArrayList<>();
        for (Entity userRole : userRoles) {
            Entity tmpRole = dbSession.getEntity("role", userRole.v("role"));
            if (tmpRole != null && !roles.contains(tmpRole)) {
                roles.add(tmpRole);
            }
        }
        return roles.contains(role);
    }

    public Entity getTopRole(DbSession dbSession, String topRoleName) {
        Entity topRole = dbSession.queryOneRawSql(
                "role",
                "name = ? AND status = ?",
                new Object[]{topRoleName, Consts.STATUS_NORMAL}
        );

        return topRole;
    }

    public List<Entity> getAllChildRoles(DbSession dbSession, Entity topRole) {
        List<Entity> roles = dbSession.queryRawSql(
                "role",
                "parentUuid = ? AND status = ?",
                new Object[]{topRole.getUuid(), Consts.STATUS_NORMAL}
        );

        return roles;
    }


}
