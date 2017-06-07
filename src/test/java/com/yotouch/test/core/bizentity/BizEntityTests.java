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




        BizMetaEntity bme2 = beMgr.getBizMetaEntityByWorkflow("party");

        Workflow wf2 = bme2.getWorkflow();
        assertEquals("party", wf2.getName());

        MetaEntity me2 = bme2.getMetaEntity();
        assertEquals(entityMgr.getMetaEntity("party"), me2);

        BizEntity partyBizModel = beService.prepareWorkflow(bme2);

        assertEquals("party", partyBizModel.getWorkflow().getName());
        assertEquals("party", partyBizModel.getEntity().v("wf_workflow"));
        assertEquals("", partyBizModel.getEntity().v("wf_state"));
        assertEquals("party", partyBizModel.getEntityModel().getWfWorkflow());
        assertEquals("", partyBizModel.getEntityModel().getWfState());

        Party partyModel = dbSession.save( partyBizModel.getEntityModel(), "party");

        assertEquals("party", partyModel.getWfWorkflow());
        assertEquals("", partyModel.getWfState());

        BizEntity beParty2 = beService.convert(wf2, partyModel);

        partyModel.setName("测试partyWorkflow");
        beParty2 = beService.doAction(dbSession, wf2.getName(), "start", partyModel, new BeforeActionHandler() {
            @Override
            public void doBeforeAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) throws WorkflowException {

            }
        }, new AfterActionHandler() {
            @Override
            public void doAfterAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) {

            }
        }, null);
        assertEquals("apply", beParty2.getState().getName());
        assertTrue(beParty2 instanceof BizEntityModelImpl);

        beParty2 = beService.doAction(dbSession, wf2.getName(), "edit", partyModel, new BeforeActionHandler() {
            @Override
            public void doBeforeAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) throws WorkflowException {

            }
        }, new AfterActionHandler() {
            @Override
            public void doAfterAction(DbSession dbSession, WorkflowAction workflowAction, Entity entity, Map<String, Object> args) {

            }
        }, null);

        assertEquals("apply", beParty2.getState().getName());
        assertTrue(beParty2 instanceof BizEntityModelImpl);
    }


}
