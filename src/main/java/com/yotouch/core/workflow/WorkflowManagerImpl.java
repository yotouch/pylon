package com.yotouch.core.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.yotouch.core.Consts;
import com.yotouch.core.config.Configure;

@Service
public class WorkflowManagerImpl implements WorkflowManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowManagerImpl.class);
    
    @Autowired
    private Configure config;
    
    Map<String, Workflow> wfMap;
    
    @Autowired
    private DbSession dbSession;
    
    public void reload() {
        this.initWorkflow();
    }
    
    @PostConstruct
    void initWorkflow() {
        this.wfMap = new HashMap<>();
        
        this.loadFileWorkflow();
        this.loadDbWorkflow();
    }
    
    private void loadFileWorkflow() {
        File ytHome = config.getRuntimeHome();

        for (File ah: ytHome.listFiles()) {
            if (ah.isDirectory()) {
                if (ah.getName().startsWith("addon-")
                        || ah.getName().startsWith("app-")) {
                    parserWorkflowConfigFile(new File(ah, "etc/workflows.yaml"));
                }
            }
        }
    }
    
    private void loadDbWorkflow() {
        List<Entity> wfList = dbSession.queryRawSql(
                "workflow",
                "status = ?",
                new Object[]{ Consts.STATUS_NORMAL }
        );
        
        for (Entity wf: wfList) {

            WorkflowImpl wfi = new WorkflowImpl(wf.getUuid(), wf.v("name"));
            
            List<Entity> stateList = dbSession.queryRawSql(
                    "workflowState",
                    "workflowUuid = ?",
                    new Object[]{wf.getUuid()}
            );
            
            Map<String, String> stateNameMap = new HashMap<>();
            for (Entity state: stateList) {
                WorkflowStateImpl wfState = new WorkflowStateImpl(state.v("name"));
                wfState.setDisplayName(state.v("displayName"));
                
                String type = "normal";
                wfState.setType(type);

                wfi.addState(wfState);
                wfState.setWorkflow(wfi);
                
                stateNameMap.put(state.getUuid(), wfState.getName());
                
            }
            
            List<Entity> actionList = dbSession.queryRawSql(
                    "workflowAction",
                    "workflowUuid = ?",
                    new Object[]{ wf.getUuid() }
            );
            
            for (Entity action: actionList) {
                WorkflowActionImpl wfAction = new WorkflowActionImpl(action.v("name"));
                wfAction.setDisplayName(action.v("displayName"));
                
                String from = action.v("fromState");
                if (Consts.WORKFLOW_STATE_ANY_STATE.equals(from)) {
                    wfAction.setFrom(WorkflowStateImpl.AnyState);
                } else {
                    WorkflowStateImpl fromState = (WorkflowStateImpl) wfi.getState(stateNameMap.get(from));
                    wfAction.setFrom(fromState);
                    fromState.addOutAction(wfAction);   
                }
                
                String toName = action.v("toState");
                if (Consts.WORKFLOW_STATE_SELF_STATE.equals(toName)) {
                    wfAction.setTo(WorkflowStateImpl.SelfState);
                } else {
                    WorkflowStateImpl toState = (WorkflowStateImpl) wfi.getState(stateNameMap.get(toName));
                    wfAction.setTo(toState);
                    toState.addInAction(wfAction);
                }

                wfi.addAction(wfAction);
                wfAction.setWorkflow(wfi);
            }

            this.wfMap.put(wf.v("name"), wfi);
        }
    }

    private void parserWorkflowConfigFile(File workflowFile) {
        logger.info("Parse workflow " + workflowFile);
        if (workflowFile.exists()) {
            Yaml yaml = new Yaml();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) yaml.load(new FileInputStream(workflowFile));

                @SuppressWarnings("unchecked")
                List<Object> workflows = (List<Object>) m.get("workflows");
                logger.info("workflows " + workflows);

                if (workflows == null) {
                    return ;
                }

                for (Object o: workflows) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> wfMap = (Map<String, Object>) o;
                    this.buildWorkflow(wfMap);
                }


            } catch (FileNotFoundException e) {
            }

        }
    }


    @SuppressWarnings("unchecked")
    private void buildWorkflow(Map<String, Object> wfMap) {
        String name = (String) wfMap.get("name");
        
        WorkflowImpl wfi = new WorkflowImpl("uuid-" + name, name);
        
        List<Map<String, String>> stateList = (List<Map<String, String>>) wfMap.get("states");
        for (Map<String, String> stMap: stateList) {
            String stateName = stMap.get("name");
            WorkflowStateImpl wfState = new WorkflowStateImpl(stateName);
            
            String stateDispName = stMap.get("displayName");
            if (StringUtils.isEmpty(stateDispName)) {
                stateDispName = stateName;
            }
            
            wfState.setDisplayName(stateDispName);
            
            String type = stMap.get("type");
            if (Consts.WORKFLOW_STATE_TYPE_START.equalsIgnoreCase(type)) {
                wfState.setType(Consts.WORKFLOW_STATE_TYPE_START);
            } else if (Consts.WORKFLOW_STATE_TYPE_FINISH.equalsIgnoreCase(type)) {
                wfState.setType(Consts.WORKFLOW_STATE_TYPE_FINISH);
            } else {
                wfState.setType(Consts.WORKFLOW_STATE_TYPE_NORMAL);
            }
            
            wfi.addState(wfState);
            wfState.setWorkflow(wfi);
        }
        
        List<Map<String, String>> actionList = (List<Map<String, String>>) wfMap.get("actions");
        for (Map<String, String> actMap: actionList) {
            String actionName = actMap.get("name");
            WorkflowActionImpl wfAction = new WorkflowActionImpl(actionName);
            
            String dispName = actMap.get("displayName");
            if (StringUtils.isEmpty(dispName)) {
                dispName = name;
            }
            wfAction.setDisplayName(dispName);
            
            
            String fromName = actMap.get("from");
            if (Consts.WORKFLOW_STATE_ANY_STATE.equalsIgnoreCase(fromName)) {
                wfAction.setFrom(WorkflowStateImpl.AnyState);
            } else {
                WorkflowStateImpl fromState = (WorkflowStateImpl) wfi.getState(fromName);
                wfAction.setFrom(fromState);
                
                fromState.addOutAction(wfAction);
            }
            
            String toName = actMap.get("to");
            if (Consts.WORKFLOW_STATE_SELF_STATE.equalsIgnoreCase(toName)) {
                wfAction.setTo(WorkflowStateImpl.SelfState);
            } else {
                WorkflowStateImpl toState = (WorkflowStateImpl) wfi.getState(toName);
                wfAction.setTo(toState);
                toState.addInAction(wfAction);
            }
            
            wfi.addAction(wfAction);
            wfAction.setWorkflow(wfi);

        }
        
        
        this.wfMap.put(name, wfi);        
    }

    @Override
    public Workflow getWorkflow(String name) {
        return this.wfMap.get(name);
    }

}
