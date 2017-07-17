package com.yotouch.test.core.bizentity;

import static org.junit.Assert.*;

import com.yotouch.base.PylonApplication;
import com.yotouch.base.bizentity.*;
import com.yotouch.base.bizentity.handler.AfterActionHandler;
import com.yotouch.base.bizentity.handler.BeforeActionHandler;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;

import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowException;
import com.yotouch.test.core.model.Party;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.workflow.Workflow;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PylonApplication.class)
public class BizEntityTests {

    @Autowired
    private EntityManager entityMgr;
    
    @Autowired
    private BizEntityManager beMgr;

    @Autowired 
    private YotouchApplication ytApp;
    
    @Autowired
    private BizEntityService beService;

    @Autowired
    private DbSession dbSession;
    
    @Test
    public void getBizEntity() {
        
        BizMetaEntity bme = beMgr.getBizMetaEntityByEntity("party");

        Workflow wf = bme.getWorkflow();
        assertEquals("party", wf.getName());

        MetaEntity me = bme.getMetaEntity();
        assertEquals(entityMgr.getMetaEntity("party"), me);

        DbSession dbSession = this.ytApp.getRuntime().createDbSession();

        BizEntity party1 = beService.prepareWorkflow(bme);
        assertEquals("party", party1.getWorkflow().getName());
        assertEquals("party", party1.getEntity().v("wf_workflow"));
        assertEquals("", party1.getEntity().v("wf_state"));

        Entity partyEntity = dbSession.save(party1.getEntity());

        assertEquals("party", partyEntity.v(Consts.BIZ_ENTITY_FIELD_WORKFLOW));
        assertEquals("", partyEntity.v(Consts.BIZ_ENTITY_FIELD_STATE));

        BizEntity beParty = beService.convert(partyEntity);

        partyEntity.setValue("quota", 10);
        partyEntity.setValue("address", "东直门麦当劳");

        beParty = beService.doAction(dbSession, "start", partyEntity);
        assertEquals("apply", beParty.getState().getName());

        beParty = beService.doAction(dbSession, "edit", partyEntity);
        assertEquals("apply", beParty.getState().getName());


    }

    @Test
    public void testWorkflowEntityModel() {
        //test get
        Party party = beMgr.getEntityModelByWorkflow("party");
        Workflow workflow = party.getWorkflow();

        assertEquals("party", party.getWfWorkflow());
        assertEquals("", party.getWfState());

        party.setName("testDoAction");
        Party started = beService.doAction(dbSession, party, "start", new BeforeActionHandler() {
            @Override
            public void doBeforeAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) throws WorkflowException {

            }

            @Override
            public <M extends EntityModel> void doBeforeAction(WorkflowAction workflowAction, M entityModel, Map<String, Object> args) throws WorkflowException {

            }
        }, new AfterActionHandler() {
            @Override
            public void doAfterAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) {

            }

            @Override
            public <M extends EntityModel> void doAfterAction(WorkflowAction workflowAction, M entityModel, Map<String, Object> args) {

            }
        }, null);
        assertEquals("apply", started.getWfState());
        assertEquals("testDoAction", started.getName());

        Party edited = beService.doAction(dbSession, party, "edit", new BeforeActionHandler() {
            @Override
            public void doBeforeAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) throws WorkflowException {

            }

            @Override
            public <M extends EntityModel> void doBeforeAction(WorkflowAction workflowAction, M entityModel, Map<String, Object> args) throws WorkflowException {

            }
        }, new AfterActionHandler() {
            @Override
            public void doAfterAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) {

            }

            @Override
            public <M extends EntityModel> void doAfterAction(WorkflowAction workflowAction, M entityModel, Map<String, Object> args) {

            }
        }, null);
        assertEquals("apply", edited.getWfState());

        assertEquals("testDoAction", edited.getName());

        //test convert
        party.setWfState("OK");
        Party convertedParty = beService.convert(workflow, party);
        assertEquals("OK", convertedParty.getWfState());

        //test convert case 2
        Party party1 = new Party();
        Party convertedParty1 = beService.convert(workflow, party1);
        assertEquals("", convertedParty1.getWfState());

        //test convert case 3
        Party party2 = new Party();
        party2.setWfWorkflow("xxx");
        party2.setWfState("start");
        Party convertedParty2 = beService.convert(workflow, party2);
        assertEquals("party", party2.getWfWorkflow());
        assertEquals("start", party2.getWfState());
        assertEquals("party", convertedParty2.getWfWorkflow());
        assertEquals("start", convertedParty2.getWfState());
    }


}
