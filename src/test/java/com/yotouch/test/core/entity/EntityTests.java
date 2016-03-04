package com.yotouch.test.core.entity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mysql.fabric.xmlrpc.base.Array;
import com.yotouch.core.PylonApplication;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaField;
import com.yotouch.core.exception.NoSuchMetaFieldException;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.runtime.YotouchRuntime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PylonApplication.class)
public class EntityTests {

    private static final Logger logger = LoggerFactory.getLogger(EntityTests.class);

    @Autowired
    private EntityManager entityMgr;

    @Autowired
    private YotouchApplication ytApp;

    @Test
    public void testLoadMetaEntities() {

        logger.info("Entity Manager " + entityMgr);

        //List<MetaEntity> metaEntities = entityMgr.getMetaEntities();
    }

    @Test
    public void testGetEntity() {

        MetaEntity me = entityMgr.getMetaEntity("attachment");

        assertNotNull(me);
        //assertEquals("meta-entity-uuid-attachment", me.getUuid());

        List<MetaEntity> metaEntities = entityMgr.getMetaEntities();

        MetaEntity me2 = null;
        for (MetaEntity tme : metaEntities) {
            if (tme.getName().equals("attachment")) {
                me2 = tme;
            }
        }

        assertEquals(me, me2);

    }

    @Test
    public void testGetSystemField() {
        MetaEntity me = entityMgr.getMetaEntity("user");
        MetaField<Date> mf = me.getMetaField("createdAt");
        assertNotNull(mf);
    }

    @Test
    public void testGetInDbSystemField() {
        MetaEntity me = entityMgr.getMetaEntity("attachment");
        MetaField<Date> mf = me.getMetaField("createdAt");
        assertNotNull(mf);
    }

    
    
    
    @Test
    public void testSaveUser() {

        YotouchRuntime rt = ytApp.getRuntime();
        
        DbSession ds = rt.createDbSession();
        logger.info("DbSession : " + ds);
        
        Entity e = ds.newEntity("user");
        e.setValue("nickname", "Tom");
        assertNull(e.getUuid());
        e = ds.save(e);
        assertNotNull(e.getUuid());
        assertEquals(e.getUuid(), e.getValue("uuid"));

        String uuid = e.getUuid();

        Entity e2 = ds.getEntity("user", uuid);
        assertEquals(e, e2);
        assertEquals(e.getValue("nickname"), "Tom");
        ds.save(e2);
        
        
        
        assertFalse(e.isFieldChanged("nickname"));
        e.setValue("nickname", "Tom");
        assertFalse(e.isFieldChanged("nickname"));
        assertNull(e.getOldValue("nickname"));
        
        e.setValue("nickname", "Jerry");
        assertTrue(e.isFieldChanged("nickname"));
        assertEquals("Tom", e.getOldValue("nickname"));
        assertEquals("Jerry", e.getValue("nickname"));
        
        ds.save(e);
        
        e2 = ds.getEntity("user", uuid);
        assertEquals(e2.getValue("nickname"), "Jerry");
        assertFalse(e2.isFieldChanged("nickname"));
        assertNull(e2.getOldValue("nickname"));
        
    }
    
    @Test(expected = NoSuchMetaFieldException.class)
    public void testGetField() {
        YotouchRuntime rt = ytApp.getRuntime();

        DbSession ds = rt.createDbSession();

        Entity e = ds.newEntity("user");
        e.getValue("no_such_field");

    }
    
    @Test
    public void testMultiReference() {
        YotouchRuntime rt = ytApp.getRuntime();
        
        DbSession dbSession = rt.createDbSession();
     
        Entity item = dbSession.newEntity("item");
        item = dbSession.save(item);
        
        Entity ip1 = dbSession.newEntity("itemProp");
        ip1 = dbSession.save(ip1);
        
        Entity ip2 = dbSession.newEntity("itemProp");
        ip2 = dbSession.save(ip2);
        
        Entity ip3 = dbSession.newEntity("itemProp");
        ip3 = dbSession.save(ip3);
        
        List<String> uuids = Arrays.asList(ip1.getUuid(), ip2.getUuid());
        
        logger.info("1111111111111111111111111111111111111");

        logger.info("mr uuids " + uuids);
        item.setValue("itemProps", uuids);
        item = dbSession.save(item);
        
        List<Entity> ips = item.mr(dbSession, "itemProps");
        assertEquals(2, ips.size());
        assertTrue(uuids.contains(ips.get(0).getUuid()));
        assertTrue(uuids.contains(ips.get(1).getUuid()));
        
        List<Entity> mappings = dbSession.queryRawSql("item_itemProps_itemProp", "itemUuid = ?", new Object[]{item.getUuid()});
        assertEquals(2, mappings.size());
        
        List<String> mappingUuids = mappings.stream().map((e)->e.getUuid()).collect(Collectors.toList());
        
        
        
        logger.info("22222222222222222222222222222222222");
        uuids = Arrays.asList(ip2.getUuid(), ip1.getUuid());
        item.setValue("itemProps", uuids);
        item = dbSession.save(item);
        
        mappings = dbSession.queryRawSql("item_itemProps_itemProp", "itemUuid = ?", new Object[]{item.getUuid()});
        assertEquals(2, mappings.size());
        
        assertTrue(mappingUuids.contains(mappings.get(0).getUuid()));
        assertTrue(mappingUuids.contains(mappings.get(1).getUuid()));
        
        logger.info("-----------------------------------------");
        uuids = Arrays.asList(ip2.getUuid(), ip3.getUuid());
        item.setValue("itemProps", uuids);
        item = dbSession.save(item);
        
        ips = item.mr(dbSession, "itemProps");
        assertEquals(2, ips.size());
        assertTrue(uuids.contains(ips.get(0).getUuid()));
        assertTrue(uuids.contains(ips.get(1).getUuid()));
        
        
        List<Entity> newMappings = dbSession.queryRawSql("item_itemProps_itemProp", "itemUuid = ?", new Object[]{item.getUuid()});
        assertEquals(2, newMappings.size());
        int contains = 0;
        Set<String> m1 = mappings.stream().map(m->m.getUuid()).collect(Collectors.toSet());
        List<String> m2 = newMappings.stream().map(m->m.getUuid()).collect(Collectors.toList());
        
        logger.info("Mappings 1 " + m1);
        logger.info("Mappings 2 " + m2);
        
        contains += m1.contains(m2.get(0)) ? 1 : 0;
        contains += m1.contains(m2.get(1)) ? 1 : 0;
        
        assertEquals(1, contains);
        
        item.setValue("itemProps", new ArrayList<String>());
        item = dbSession.save(item);
        ips = item.mr(dbSession, "itemProps");
        assertEquals(0, ips.size());
        
        mappings = dbSession.queryRawSql("item_itemProps_itemProp", "itemUuid = ?", new Object[]{item.getUuid()});
        assertEquals(0, mappings.size());
        
        
    }
    
}
