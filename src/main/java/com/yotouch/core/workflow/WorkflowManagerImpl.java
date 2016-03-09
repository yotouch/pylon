package com.yotouch.core.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
    
    @PostConstruct
    void initWorkflow() {
        this.wfMap = new HashMap<>();
        
        this.loadFileWorkflow();
    }
    
    private void loadFileWorkflow() {
        File ytEctDir = config.getEtcDir();
        File workflowFile = new File(ytEctDir, "workflows.yaml");
        if (workflowFile.exists()) {
            Yaml yaml = new Yaml();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) yaml.load(new FileInputStream(workflowFile));

                @SuppressWarnings("unchecked")
                List<Object> workflows = (List<Object>) m.get("workflows");
                logger.info("workflows " + workflows);
                
                for (Object o: workflows) {
                    Map<String, Object> wfMap = (Map<String, Object>) o;
                    this.buildWorkflow(wfMap);
                }
                
                
            } catch (FileNotFoundException e) {
            }

        }
        
    }
    

    private void buildWorkflow(Map<String, Object> wfMap) {
        String name = (String) wfMap.get("name");
        
        WorkflowImpl wfi = new WorkflowImpl(name);
        
        List<Map<String, String>> stateList = (List<Map<String, String>>) wfMap.get("states");
        for (Map<String, String> stMap: stateList) {
            String stateName = stMap.get("name");
            WorkflowStateImpl wfState = new WorkflowStateImpl(stateName);
            
            String stateDispName = stMap.get("displayName");
            if (StringUtils.isEmpty(stateDispName)) {
                stateDispName = "";
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
        
        
        this.wfMap.put(name, wfi);        
    }

    @Override
    public Workflow getWorkflow(String name) {
        return this.wfMap.get(name);
    }

}
