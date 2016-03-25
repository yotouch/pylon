package com.yotouch.test.core.bizentity;

import static org.junit.Assert.*;

import com.yotouch.base.bizentity.BizEntity;
import com.yotouch.core.runtime.DbSession;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yotouch.core.PylonApplication;
import com.yotouch.base.bizentity.BizEntityManager;
import com.yotouch.base.bizentity.BizMetaEntity;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.workflow.Workflow;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PylonApplication.class)
public class BizEntityTests {

    @Autowired
    private BizEntityManager beMgr;

    @Autowired
    private YotouchApplication ytApp;

    @Test
    public void getBizEntity() {

        BizMetaEntity bme = beMgr.getBizMetaEntity("party");

        Workflow wf = bme.getWorkflow();
        assertEquals("party", wf.getName());

        DbSession dbSession = this.ytApp.getRuntime().createDbSession();

        BizEntity party1 = bme.prepareWorkflow();




    }


}
