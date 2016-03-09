package com.yotouch.test.core.workflow;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yotouch.core.PylonApplication;
import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowManager;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PylonApplication.class)
public class WorkflowTests {
    
    @Autowired
    private WorkflowManager wfMgr;
    
    @Test
    public void loadWorkflow() {
        
        Workflow wf = wfMgr.getWorkflow("testWf");
        
        assertEquals("testWf", wf.getName());
        
        
        
        
    }

}
