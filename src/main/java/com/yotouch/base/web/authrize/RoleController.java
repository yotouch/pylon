package com.yotouch.base.web.authrize;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.yotouch.base.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yotouch.core.ErrorCode;
import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.base.service.PaginationService;
import com.yotouch.base.web.controller.BaseController;

@Controller
public class RoleController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private PaginationService paginationService;
    
    @Autowired
    private RoleService roleService;

    @RequestMapping("/admin/role/list")
    public String listMenu(
            @RequestParam(value="currentPage", defaultValue= "1") int currentPage,
            Model model
    ) {

        int itemPerPage = Consts.itemPerPage;
        DbSession dbSession = this.ytApp.getRuntime().createDbSession();
        List<Entity> roles = dbSession.queryRawSql("role", "status=?", new Object[]{Consts.STATUS_NORMAL});

        int total = roles.size();
        if (total > 0){
            Object[] queryCondition = {Consts.STATUS_NORMAL};
            Map<String, Object> paginationInfo = paginationService.getPaginationInfo(currentPage, total, itemPerPage, "role", "status=? ", queryCondition, "/admin/role/list?currentPage=");
            model.addAttribute("paginationInfo", paginationInfo.get("paginationInfo"));
            model.addAttribute("roles", paginationInfo.get("role"));
        }else{
            model.addAttribute("roles", roles);
        }

        Map<String, List<Entity>> roleMenus = new HashMap<String, List<Entity>>();
        roleMenus = addMenus(roles);
        model.addAttribute("roleMenus", roleMenus);

        return "/admin/role/list";
    }


    protected Map<String, List<Entity>> addMenus(List<Entity> roles){

        Map<String, List<Entity>> results = new HashMap<String, List<Entity>>();
        for(Entity role : roles) {
            List<Entity> menus = new ArrayList<Entity>();
            menus = getMenus(role);
            results.put(role.getUuid(), menus);
        }
        return results;
    }

    protected List<Entity> getMenus(Entity role){

        DbSession dbSession = this.ytApp.getRuntime().createDbSession();
        List<Entity> menus = new ArrayList<Entity>();
        List<Entity> roleMenus =  dbSession.queryRawSql("roleMenu", "roleUuid=? AND status=?", new Object[]{role.getUuid(), Consts.STATUS_NORMAL});
        if (roleMenus != null){
            for (Entity m : roleMenus) {
                String uuid = m.v("menu");
                Entity menu = dbSession.getEntity("menu", uuid);
                if (menu != null) {
                    menus.add(menu);
                }
            }
        }

        return menus;
    }


    @RequestMapping(value="/admin/role/edit", method=RequestMethod.GET)
    public String edit(
            @RequestParam(value="uuid", defaultValue="") String uuid,
            @RequestParam(value="errorCode", defaultValue="") String errorCode,
            Model model
    ) {

        DbSession dbSession = this.getDbSession();
        Entity role = dbSession.getEntity("role", uuid);

        if (role != null) {
            List<Entity> currentMenus = getMenus(role);
            model.addAttribute("currentMenus", currentMenus);
        }

        model.addAttribute("role", role);

        List<Entity> topRoles = dbSession.queryRawSql("role", "parentUuid = ? AND status = ?", new Object[]{"", Consts.STATUS_NORMAL});

        model.addAttribute("topRoles", topRoles);

        List<Entity> menus = dbSession.queryRawSql("menu", "status=?", new Object[]{Consts.STATUS_NORMAL});

        model.addAttribute("menus", menus);

        if(!StringUtils.isEmpty(errorCode)) {
            model.addAttribute("errorCode", errorCode);
        }

        return "/admin/role/edit";
    }


    @RequestMapping(value="/admin/role/edit", method=RequestMethod.POST)
    public String doedit(
            @RequestParam(value="uuid", defaultValue="") String uuid,
            @RequestParam(value ="menu", defaultValue="") String[] menu,
            HttpServletRequest request,
            RedirectAttributes redirectAttr,
            Model model
    ) {

        DbSession dbSession = this.getDbSession();
        Entity role = null;
        if (!StringUtils.isEmpty(uuid)) {
            role = dbSession.getEntity("role", uuid);
        }


        if (role != null) {
            deleteCurrentRoleMenus(role);
            role = webUtil.updateEntityVariables(role, request);
            role = dbSession.save(role);

        } else {
            role = dbSession.newEntity("role", Consts.STATUS_NORMAL);
            role = webUtil.updateEntityVariables(role, request);

            Entity oldRole = dbSession.queryOneRawSql("role",
                    "name = ? AND status = ?",
                    new Object[]{role.v("name"), Consts.STATUS_NORMAL}
            );

            if (oldRole != null) {
                redirectAttr.addAttribute("errorCode", ErrorCode.INPUT_ERROR);
                return "redirect:/admin/role/edit";
            }

            role = dbSession.save(role);

        }

        for (String m : menu) {
            Entity roleMenu =  dbSession.newEntity("roleMenu", Consts.STATUS_NORMAL);
            roleMenu.setValue("role", role.getUuid());
            roleMenu.setValue("menu", m);
            dbSession.save(roleMenu);
        }

        return "redirect:/admin/role/list";
    }

    protected void deleteCurrentRoleMenus(Entity role){

        DbSession dbSession = this.getDbSession();
        List<Entity> roleMenus = dbSession.queryRawSql("roleMenu", "roleUuid = ? AND status = ?", new Object[]{role.getUuid(), Consts.STATUS_NORMAL});

        for (Entity m : roleMenus) {
            m.setValue("status", Consts.STATUS_DELETED);
            dbSession.save(m);
        }
    }
    
    @RequestMapping("/admin/role/user")
    public String userList(
            @RequestParam("uuid") String uuid,
            HttpServletRequest request,
            Model model
    ) {
        DbSession dbSession = this.getDbSession(request);
        Entity role = dbSession.getEntity("role", uuid);
        
        List<Entity> userList = roleService.getUserList(dbSession, role);
        
        model.addAttribute("role", role);
        model.addAttribute("userList", userList);
        
        return "/admin/role/user";
    }
    
    @RequestMapping("/admin/role/addUser")
    public String addUser(
            @RequestParam("role") String roleUuid,
            @RequestParam("user") String userUuid,
            HttpServletRequest request
    ) {
        DbSession dbSession = this.getDbSession(request);
        
        Entity role = dbSession.getEntity("role", roleUuid);
        Entity user = dbSession.getEntity("user", userUuid);
        
        roleService.addRole(dbSession, user, role);
        return "redirect:/admin/role/user?uuid=" + roleUuid;
    }

    @RequestMapping("/admin/role/removeUser")
    public String removeUser(
            @RequestParam("role") String roleUuid,
            @RequestParam("user") String userUuid,
            HttpServletRequest request
    ) {
        DbSession dbSession = this.getDbSession(request);

        Entity role = dbSession.getEntity("role", roleUuid);
        Entity user = dbSession.getEntity("user", userUuid);
        
        roleService.removeUserRole(dbSession, user, role);
        return "redirect:/admin/role/user?uuid=" + roleUuid;
    }


}
