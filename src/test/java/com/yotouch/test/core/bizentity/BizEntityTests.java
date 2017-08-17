package com.yotouch.test.core.bizentity;

import static org.junit.Assert.*;

import com.yotouch.base.PylonApplication;
import com.yotouch.base.bizentity.BizEntity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.base.bizentity.BizEntityManager;
import com.yotouch.base.bizentity.BizEntityService;
import com.yotouch.base.bizentity.BizMetaEntity;
import com.yotouch.core.workflow.Workflow;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = PylonApplication.class)
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

    }


}
