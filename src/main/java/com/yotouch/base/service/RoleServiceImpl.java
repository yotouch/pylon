package com.yotouch.base.service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.runtime.YotouchRuntime;
import com.yotouch.base.web.util.WebUtil;

@Service
public class RoleServiceImpl implements RoleService {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    
    @Autowired
    protected YotouchApplication ytApp;

    @Autowired
    protected WebUtil webUtil;

    public List<Entity> getRole(Entity user){
        YotouchRuntime rt = ytApp.getRuntime();
        
        DbSession dbSession = rt.createDbSession();

        List<Entity> userRoles = dbSession.queryRawSql("userRole", "userUuid=? AND status=?", new Object[]{user.v("uuid"), Consts.STATUS_NORMAL});

        return userRoles;
    }

    public List<Entity> getMenu(List<Entity> userRoles){
        YotouchRuntime rt = ytApp.getRuntime();
        
        DbSession dbSession = rt.createDbSession();

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

        YotouchRuntime rt = ytApp.getRuntime();
        DbSession dbSession = rt.createDbSession();

        for (String roleUuid : roles) {

            Entity userRole = dbSession.newEntity("userRole", Consts.STATUS_NORMAL);
            userRole.setValue("role", roleUuid);
            userRole.setValue("user", user.getUuid());
            dbSession.save(userRole);

        }
        
    }

    @Transactional
    public void deleteUserRoles(Entity user){
        YotouchRuntime rt = ytApp.getRuntime();
        DbSession dbSession = rt.createDbSession();
        
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
        YotouchRuntime rt = ytApp.getRuntime();
        DbSession dbSession = rt.createDbSession();
        List<Entity> roles = dbSession.queryRawSql("role", "parentUuid = ? AND status = ?", new Object[]{parentRoleUuid, Consts.STATUS_NORMAL});
        return roles;
    }
    
    public List<Entity> getUserList(List<Entity> roles){
        YotouchRuntime rt = ytApp.getRuntime();
        DbSession dbSession = rt.createDbSession();
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

        YotouchRuntime rt = ytApp.getRuntime();
        DbSession dbSession = rt.createDbSession();

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
        List<Entity> roles = this.getRole(user);
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
