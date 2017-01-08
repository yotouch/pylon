package com.yotouch.base.web.designer;

import com.yotouch.base.bizentity.BizEntityManager;
import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaEntity;
import me.chanjar.weixin.common.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yotouch.base.web.controller.BaseController;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WfAdminDesignController extends BaseController {

    @Autowired
    private BizEntityManager beMgr;
    
    @RequestMapping("/_wf_/wf/list")
    public String wfList(
            HttpServletRequest request,
            Model model
    ) {
        
        DbSession dbSession = this.getDbSession(request);
        
        List<Entity> workflowList = dbSession.queryRawSql(
                "workflow",
                "status = ? ORDER BY createdAt ASC",
                new Object[]{Consts.STATUS_NORMAL}
        );
        model.addAttribute("workflowList", workflowList);

        Map<String, MetaEntity> meMap = new HashMap<>();
        for (Entity wf: workflowList) {
            Entity be = dbSession.queryOneRawSql(
                    "bizEntity",
                    "workflowUuid=?",
                    new Object[]{wf.getUuid()}
            );
            
            meMap.put(wf.getUuid(), this.ytApp.getEntityManager().getMetaEntity(be.v("metaEntity")));
        }
        model.addAttribute("meMap", meMap);
        
        return "/admin/workflow/list";
    }
    
    @RequestMapping(value = "/_wf_/wf/edit", method = RequestMethod.GET)
    public String edit(
            @RequestParam(value = "uuid", defaultValue = "") String wfUuid,
            Model model
    ) {

        DbSession dbSession = this.getDbSession();
        Entity wfEntity = null;
        
        if (!StringUtils.isEmpty(wfUuid)) {
            wfEntity = dbSession.getEntity("workflow", wfUuid);
        }
        
        if (wfEntity == null) {
            wfEntity = dbSession.newEntity("workflow");
        }
        
        model.addAttribute("workflow", wfEntity);


        Entity be = dbSession.queryOneRawSql(
                "bizEntity",
                "workflowUuid = ?",
                new Object[]{ wfEntity.getUuid() }
        );

        if (be == null) {
            be = dbSession.newEntity("bizEntity");
        }
        model.addAttribute("bizEntity", be);


        List<MetaEntity> metaEntityList = this.ytApp.getEntityManager().getMetaEntities();
        model.addAttribute("metaEntityList", metaEntityList);


        return "/admin/workflow/edit";
    }
    
    @RequestMapping(value = "/_wf_/wf/edit", method = RequestMethod.POST)
    public String doEdit(
            @RequestParam(value = "uuid", defaultValue = "") String wfUuid,
            @RequestParam(value = "metaEntity", defaultValue = "") String metaEntity,
            HttpServletRequest request,
            Model model
    ) {
        
        DbSession dbSession = this.getDbSession(request);
        
        Entity wfEntity = null;
        if (!StringUtils.isEmpty(wfUuid)) {
            wfEntity = dbSession.getEntity("workflow", wfUuid);
        } else {
            wfEntity = dbSession.newEntity("workflow");
        }
        
        wfEntity = webUtil.updateEntityVariables(wfEntity, request);
        wfEntity = dbSession.save(wfEntity);
        
        Entity be = dbSession.queryOneRawSql(
                "bizEntity",
                "workflowUuid = ?",
                new Object[]{wfEntity.getUuid()}
        );
        
        if (be == null) {
            be = dbSession.newEntity("bizEntity");
        }
        
        be.setValue("workflow", wfEntity);
        be.setValue("metaEntity", metaEntity);
        dbSession.save(be);

        return "redirect:/_wf_/wf/build?uuid=" + wfEntity.getUuid();
    }
    
    @RequestMapping("/_wf_/wf/build")
    public String buildWf(
            @RequestParam("uuid") String wfUuid,
            Model model
    ) {
        DbSession dbSession = this.getDbSession();
        Entity wf = dbSession.getEntity("workflow", wfUuid);
        model.addAttribute("workflow", wf);
        
        Entity s = dbSession.queryOneRawSql(
                "workflowState",
                "workflowUuid = ? AND type = ?",
                new Object[]{ wfUuid, Consts.WORKFLOW_STATE_TYPE_START }
        );
        if (s == null) {
            s = dbSession.newEntity("workflowState");
            s.setValue("type", Consts.WORKFLOW_STATE_TYPE_START);
            s.setValue("name", "start");
            s.setValue("displayName", "Start");
            s.setValue("workflow", wf);
            dbSession.save(s);
        }

        s = dbSession.queryOneRawSql(
                "workflowState",
                "workflowUuid = ? AND type = ?",
                new Object[]{ wfUuid, Consts.WORKFLOW_STATE_TYPE_FINISH }
        );
        if (s == null) {
            s = dbSession.newEntity("workflowState");
            s.setValue("type", Consts.WORKFLOW_STATE_TYPE_FINISH);
            s.setValue("name", "finish");
            s.setValue("displayName", "Finish");
            s.setValue("workflow", wf);
            dbSession.save(s);
        }
        
        List<Entity> wfStateList = dbSession.queryRawSql(
                "workflowState",
                "workflowUuid = ? ORDER BY createdAt ASC",
                new Object[]{ wf.getUuid() }
        );
        model.addAttribute("workflowStateList", wfStateList);

        List<Entity> wfActionList = dbSession.queryRawSql(
                "workflowAction",
                "workflowUuid = ? ORDER BY createdAt ASC",
                new Object[]{ wf.getUuid() }
        );
        model.addAttribute("workflowActionList", wfActionList);

        return "/admin/workflow/build";        
    }
    
    @RequestMapping("/_wf_/wf/editState")
    public String addState(
            @RequestParam(value = "wfUuid") String wfUuid,
            @RequestParam(value = "uuid", defaultValue = "") String uuid,
            HttpServletRequest request
    ) {
        DbSession dbSession = this.getDbSession(request);
        
        Entity workflow = dbSession.getEntity("workflow", wfUuid);
        
        Entity state = null;
        if (StringUtils.isEmpty(uuid)) {
            state = dbSession.newEntity("workflowState");
        } else {
            state = dbSession.getEntity("workflowState", uuid);
        }
        
        state = webUtil.updateEntityVariables(state, request);
        state.setValue("workflow", workflow);
        state.setValue("type", Consts.WORKFLOW_STATE_TYPE_NORMAL);
        dbSession.save(state);
        
        return "redirect:/_wf_/wf/build?uuid=" + wfUuid;
    }
    
    @RequestMapping("/_wf_/wf/editAction")
    public String editAction(
            @RequestParam("wfUuid") String wfUuid,
            @RequestParam(value = "uuid", defaultValue = "") String uuid,
            HttpServletRequest request
    ) {
        DbSession dbSession = this.getDbSession(request);
        
        Entity workflow = dbSession.getEntity("workflow", wfUuid);

        Entity action = null;
        if (StringUtils.isEmpty(uuid)) {
            action = dbSession.newEntity("workflowAction");
        } else {
            action = dbSession.getEntity("workflowAction", uuid);
        }
        
        action = webUtil.updateEntityVariables(action, request);
        action.setValue("workflow", workflow);
        dbSession.save(action);
        
        return "redirect:/_wf_/wf/build?uuid=" + wfUuid;
    }
    
    @RequestMapping("/_wf_/wf/reload")
    public String reload() {
        beMgr.reload();
        return "redirect:/_wf_/wf/list";
    }
    
}
