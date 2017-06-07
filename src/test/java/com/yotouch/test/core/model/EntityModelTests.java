package com.yotouch.test.core.model;

import com.yotouch.base.PylonApplication;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by king on 6/7/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PylonApplication.class)
public class EntityModelTests {
    @Autowired
    private DbSession dbSession;

    @Test
    public void testEntityModelUuid() {
        Party party = new Party();
        String genUuid = party.getUuid();
        assertNotNull(genUuid);
        assertTrue(genUuid.startsWith("-"));

        Party save1 = dbSession.save(party, "party");
        String save1Uuid = save1.getUuid();
        assertNotNull(save1Uuid);
        assertFalse(save1Uuid.startsWith("-"));

        Entity entity = dbSession.newEntity("party");
        Entity save = dbSession.save(entity);
        Party party1 = save.looksLike(Party.class);
        assertNotNull(party1.getUuid());
        assertFalse(party1.getUuid().startsWith("-"));
    }
}
