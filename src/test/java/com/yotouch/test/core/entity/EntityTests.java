package com.yotouch.test.core.entity;

import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.Collectors;

import com.yotouch.core.entity.*;
import com.yotouch.test.core.model.Party;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yotouch.base.PylonApplication;
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

        //test int default value is null, not 0
        assertNull(e2.getValue("age"));
        assertFalse(e2.isFieldChanged("age"));

        //test long default value is null, not 0l;
        assertNull(e2.getValue("lastTouched"));
        assertFalse(e2.isFieldChanged("lastTouched"));

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
        assertEquals(ip1.getUuid(), ips.get(0).getUuid());
        assertEquals(ip2.getUuid(), ips.get(1).getUuid());
        
        List<Entity> mappings = dbSession.queryRawSql("item_itemProps_itemProp", "s_itemUuid = ?", new Object[]{item.getUuid()});
        assertEquals(2, mappings.size());
        
        List<String> mappingUuids = mappings.stream().map((e)->e.getUuid()).collect(Collectors.toList());
        
        
        
        logger.info("22222222222222222222222222222222222");
        uuids = Arrays.asList(ip2.getUuid(), ip1.getUuid());
        item.setValue("itemProps", uuids);
        item = dbSession.save(item);
        
        mappings = dbSession.queryRawSql("item_itemProps_itemProp", "s_itemUuid = ?", new Object[]{item.getUuid()});
        assertEquals(2, mappings.size());
        
        assertTrue(mappingUuids.contains(mappings.get(0).getUuid()));
        assertTrue(mappingUuids.contains(mappings.get(1).getUuid()));

        logger.info("----------------------------------------");
        uuids = Arrays.asList(ip2.getUuid(), ip3.getUuid());
        item.setValue("itemProps", uuids);
        item = dbSession.save(item);

        logger.info("Set mr " + uuids);
        ips = item.mr(dbSession, "itemProps");
        logger.info("Get mr " + ips);
        assertEquals(2, ips.size());
        assertTrue(uuids.contains(ips.get(0).getUuid()));
        assertTrue(uuids.contains(ips.get(1).getUuid()));
        assertEquals(ip2.getUuid(), ips.get(0).getUuid());
        assertEquals(ip3.getUuid(), ips.get(1).getUuid());
        
        
        List<Entity> newMappings = dbSession.queryRawSql("item_itemProps_itemProp", "s_itemUuid = ?", new Object[]{item.getUuid()});
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
        
        mappings = dbSession.queryRawSql("item_itemProps_itemProp", "s_itemUuid = ?", new Object[]{item.getUuid()});
        assertEquals(0, mappings.size());


        Entity item2 = dbSession.newEntity("item");
        List<Entity> related = new ArrayList<>();
        related.add(item);
        item2.setValue("relatedItems", related);
        item2 = dbSession.save(item2);
        List<Entity> newRelated = item2.mr(dbSession, "relatedItems");
        assertEquals(1, newRelated.size());
        assertEquals(item, newRelated.get(0));
    }

    @Test
    public void testLong() {

        YotouchRuntime rt = ytApp.getRuntime();

        DbSession ds = rt.createDbSession();
        logger.info("DbSession : " + ds);

        Entity e = ds.newEntity("user");
        e.setValue("nickname", "Long");
        assertNull(e.getUuid());
        e.setValue("lastTouched", System.currentTimeMillis());
        e = ds.save(e);

        Entity e1 = ds.getEntity("user", e.getUuid());

        long lt = e1.v("lastTouched");

        assertEquals((long)e.v("lastTouched"), lt);

    }

    @Test
    public void testDefaultValueFromYaml() {

        YotouchRuntime rt = ytApp.getRuntime();

        DbSession ds = rt.createDbSession();
        logger.info("DbSession : " + ds);

        Entity e = ds.newEntity("party");
        assertEquals((String)e.v("name"), "默认party");

        e = ds.save(e);
        assertEquals("默认party", (String) e.v("name"));
    }

    @Test
    public void testAsPojo() {
        YotouchRuntime rt = ytApp.getRuntime();

        DbSession ds = rt.createDbSession();
        logger.info("DbSession : " + ds);

        Entity e1 = ds.newEntity("user");
        Entity e2 = ds.newEntity("user");

        HashMap<String, Object> map = new HashMap<>();
        map.put("nickname", "king");
        map.put("age", 14);
        e1.fromMap(map);

        e2.setValue("nickname", "king");
        e2.setValue("age", 14);

        assertEquals(e2, e1);

        Party party = new Party();
        party.setName("testModelParty");

        assertNotNull(party.getUuid());

        Party party1 = ds.save(party, "party");

        assertNotNull(party1.getUuid());

        assertEquals(party, party1);

        Party party2 = ds.getEntity("party", party1.getUuid(), Party.class);

        assertEquals(party1.getUuid(), party2.getUuid());
    }
    
    @Test
    public void outterUuid() {
        YotouchRuntime rt = ytApp.getRuntime();

        DbSession ds = rt.createDbSession();
        logger.info("DbSession : " + ds);

        Entity u1 = ds.newEntity("user");
        u1.setValue("nickname", "UUID name");
        
        String uuid = UUID.randomUUID().toString();
        u1.setValue("uuid", "-" + uuid);
        
        assertTrue(u1.isNew());
        u1 = ds.save(u1);
        assertEquals(uuid, u1.getUuid());

        Entity u2 = ds.getEntity("user", uuid);
        assertEquals(u2, u1);
                
    }

    @Test
    public void valueOptions() {
        YotouchRuntime rt = ytApp.getRuntime();

        DbSession ds = rt.createDbSession();

        Entity e = ds.newEntity("metaField");

        MetaField<?> dataType = e.getMetaEntity().getMetaField("dataType");
        ValueOption stringOption = dataType.getValueOption("STRING");

        assertEquals(10, dataType.getValueOptions().size(), 0);
        assertEquals("STRING", stringOption.getDisplayName());
        assertEquals(0, stringOption.getWeight(), 0);
        assertEquals("STRING", stringOption.getPinYin());
        assertEquals("metaField-dataType-STRING", stringOption.getValue());
        assertEquals("dataType", stringOption.getMetaField().getName());
        assertEquals("metaField", stringOption.getMetaEntity().getName());
        assertEquals(dataType, stringOption.getMetaField());
        assertEquals(e.getMetaEntity(), stringOption.getMetaEntity());


        e = ds.newEntity("user");
        MetaField<?> status = e.getMetaEntity().getMetaField("status");
        ValueOption statusOption = status.getValueOption("删除");

        assertEquals(3, status.getValueOptions().size(), 0);
        assertEquals("删除", statusOption.getDisplayName());
        assertEquals(1, statusOption.getWeight(), 0);
        assertEquals("SHANCHU", statusOption.getPinYin());
        assertEquals("user-status-删除", statusOption.getValue());
        assertEquals("status", statusOption.getMetaField().getName());
        assertEquals(status, statusOption.getMetaField());  //一个是原始的systemField 一个是后来的metaField表中的一个field
        assertEquals(e.getMetaEntity(), statusOption.getMetaEntity());
    }
}
