package com.yotouch.test.core.workflow;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowManager;
import com.yotouch.core.workflow.WorkflowState;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkflowTests {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowTests.class);
    
    @Autowired
    private WorkflowManager wfMgr;
    
    @Test
    public void loadWorkflow() {
        
        Workflow wf = wfMgr.getWorkflow("testWf");
        
        assertEquals("testWf", wf.getName());
        
        WorkflowState startState = wf.getStartState();
        assertEquals("start", startState.getName());
        
        List<WorkflowState> finishStates = wf.getFinishStates();
        assertEquals(1, finishStates.size());
        
        WorkflowState finishState = finishStates.get(0);
        assertEquals("finish", finishState.getName());
        
        
        List<WorkflowAction> actions = startState.getOutActions();
        assertEquals(3, actions.size());
        
        WorkflowAction startAction = actions.get(0);
        assertEquals("start", startAction.getName());
        
        WorkflowState state3 = wf.getState("state3");
        List<WorkflowAction> inActions = state3.getInActions();
        assertEquals(3, inActions.size());
        
        WorkflowAction editAction = wf.getAction("edit");
        WorkflowAction reviewAction = wf.getAction("review3");
        WorkflowAction act1to3Action = wf.getAction("act1_3");
        
        logger.info("inActions " + inActions);
        logger.info("editAction " + editAction);
        logger.info("reviewAction " + reviewAction);
        logger.info("act1to3Action " + act1to3Action);
        
        assertTrue(inActions.contains(editAction));
        assertTrue(inActions.contains(reviewAction));
        assertTrue(inActions.contains(act1to3Action));
        
        WorkflowAction state3EditAction = null;
        WorkflowAction state3review3Action = null;
        WorkflowAction state3FinishAction = null;
        WorkflowAction state3To2Action = null;
        List<WorkflowAction> outActions = state3.getOutActions();
        logger.info("outActions " + outActions);
        assertEquals(5, outActions.size());
        
        for (WorkflowAction wfa: outActions) {
            if (wfa.getName().equals("edit")) {
                state3EditAction = wfa;
            } else if (wfa.getName().equals("review3")) {
                state3review3Action = wfa;
            } else if (wfa.getName().equals("act3_finish")) {
                state3FinishAction = wfa;
            } else if (wfa.getName().equals("act3_2")) {
                state3To2Action = wfa;
            }
        }
        
        assertNotNull(state3EditAction);
        assertEquals(state3, state3EditAction.getFrom());
        assertEquals(state3, state3EditAction.getTo());
        
        assertNotNull(state3review3Action);
        assertEquals(state3, state3review3Action.getFrom());
        assertEquals(state3, state3review3Action.getTo());
        
        assertNotNull(state3FinishAction);
        assertEquals(state3, state3FinishAction.getFrom());
        assertEquals(finishState, state3FinishAction.getTo());
        
        assertNotNull(state3To2Action);
        assertEquals(state3, state3To2Action.getFrom());
        assertEquals("state2", state3To2Action.getTo().getName());
        
        
        
        
        
        
        
    }

}
