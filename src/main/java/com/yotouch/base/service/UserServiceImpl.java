package com.yotouch.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.runtime.YotouchRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    static final private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private YotouchApplication ytApp;

    @Autowired
    private RoleService roleService;

    @Override
    public String genPassword(Entity user, String password) {
        //// TODO: 16/5/12 从 application.yaml 中获取 
        String instanceName = "yotouch";
        
        logger.info("Instance name " + instanceName);
        String pwdStr = "yotouch:" + instanceName + ":" + user.getUuid() + ":" + password;
        logger.info("Gen plain password " + pwdStr);
        String md5Pwd = DigestUtils.md5DigestAsHex(pwdStr.getBytes());
        return md5Pwd;
    }

    @Override
    public String genLoginToken(Entity user) {
        
        String token = "1." + user.getMetaEntity().getName() + ".";
        
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("uuid", user.getUuid());
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            String dataStr = mapper.writeValueAsString(dataMap);
            logger.info("Token data string " + dataStr);
            long now = System.currentTimeMillis() / 1000; // 单位到秒

            String dataInfo = Base64Utils.encodeToString(dataStr.getBytes());
            token = token + dataInfo + "." + now;

            String vcodeToken = user.v("password") + token;
            logger.info("Gen vcodeToken " + vcodeToken);
            String vcode = DigestUtils.md5DigestAsHex(vcodeToken.getBytes()).substring(8, 16);
            token += "." + vcode;

            logger.info(" token " + token);

            return token;
            
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Entity checkLoginUser(String userToken) {
        
        if (StringUtils.isEmpty(userToken)) {
            return null;
        }
        
        // "1.user.eyJ1dWlkIjoiYjc5MDQzYzEtZTk1ZC00ZjY0LWI1ZGMtYWUyYTI1Y2M0NGY0In0=.1455088972.a4e765cd"   

        String[] parts = userToken.split("\\.");
        if (parts.length == 5) {
            String formatVersion = parts[0];
            String type          = parts[1];
            String unInfoStr       = parts[2];
            String genTime       = parts[3];
            String vcode         = parts[4];

            if ("1".equals(formatVersion)) {
                String infoStr = new String(Base64Utils.decodeFromString(unInfoStr));

                ObjectMapper mapper = new ObjectMapper();
                try {
                    Map<String, String> map = mapper.readValue(infoStr, new TypeReference<Map<String, String>>() {});
                    String uuid = map.get("uuid");
                    YotouchRuntime runtime = ytApp.getRuntime();
                    DbSession dbSession = runtime.createDbSession();
                    Entity user = dbSession.getEntity(type, uuid);

                    if (user != null) {
                        String vcodeToken = user.v("password") + formatVersion + "." + type + "." + unInfoStr + "." + genTime;
                        logger.debug("Check vcodeToken " + vcodeToken);
                        String otherVcode = DigestUtils.md5DigestAsHex(vcodeToken.getBytes()).substring(8, 16);
                        if (!otherVcode.equals(vcode)) {
                            return null;
                        }
                    }

                    return user;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return null;
    }

    @Override
    public Entity modifyPassword(Entity user, String password, String newPassword) {
        YotouchRuntime runtime = ytApp.getRuntime();
        DbSession dbSession = runtime.createDbSession();
        Entity u = dbSession.queryOneRawSql(user.getMetaEntity().getName(), "password = ? AND status = ?", new Object[]{genPassword(user, password), Consts.STATUS_NORMAL});
        if (u == null){
            return null;
        }

        u.setValue("password", genPassword(u, newPassword));

        return dbSession.save(u);
    }

    @Override
    public void seedLoginCookie(HttpServletResponse response, Entity user) {
        String userToken = this.genLoginToken(user);
        logger.info("userToken: " + userToken);
        Cookie c = new Cookie("userToken", userToken);
        c.setPath("/");
        response.addCookie(c);
    }

    public List<Entity> getUsersByRole(DbSession dbSession, Entity role, int status) {

        List<Entity> users = new ArrayList<Entity>();
        List<Entity> roles = roleService.getAllChildRoles(dbSession, role);
        roles.add(role);
        List<Entity> userRoles = getUserRoles(dbSession, roles);

        for (Entity userRole : userRoles) {
            Entity user = dbSession.getEntity("user", userRole.v("user")) ;
            if (user != null && !users.contains(user) && user.v("status").equals(status)) {
                users.add(user);
            }
        }

        return users;
    }

    public List<Entity> getUserRoles(DbSession dbSession, List<Entity> roles) {
        List<Entity> results = new ArrayList<Entity>();

        for (Entity role : roles) {
            List<Entity> tmpUserRoles = dbSession.queryRawSql(
                    "userRole",
                    "roleUuid = ? AND status = ?",
                    new Object[]{role.getUuid(), Consts.STATUS_NORMAL}
            );

            if (tmpUserRoles.size() > 0) {
                results.addAll(tmpUserRoles);
            }
        }

        return results;
    }

    @Override
    public List<Entity> getUserRoleList(DbSession dbSession, Entity user) {
        List<Entity> roles = roleService.getUserRoles(user);
        return roles.stream().map(ur->ur.sr(dbSession, "role")).collect(Collectors.toList());
    }

    @Override
    public boolean checkPassword(DbSession dbSession, Entity user, String password) {
        String oldPwd = user.v("password");
        if (oldPwd.startsWith("plain:")) {
            oldPwd = oldPwd.substring(6, oldPwd.length());
            if (oldPwd.equals(password)) {
                
                user.setValue("password", this.genPassword(user, password));
                dbSession.save(user);
                return true;
            }
        } else {
            password = this.genPassword(user, password);
            if (password.equals(oldPwd)) {
                return true;
            }
        }
        
        return false;
    }

}
