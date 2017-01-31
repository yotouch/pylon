package com.yotouch.test.core.entity;

import static org.junit.Assert.*;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaField;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yotouch.base.PylonApplication;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PylonApplication.class)
@Transactional
public class ReferenceEntityTests {
    
    @Autowired
    private DbSession dbSession;
    
    @Autowired
    private EntityManager entityManager;
    
    @Test
    public void testReferenceValue() {

        Entity user = dbSession.newEntity("user");
        Entity userAttr = dbSession.newEntity("userAttr");
        Entity passport = dbSession.newEntity("passport");
        
        passport.setValue("number", "GA12345678");
        passport.setValue("issuePlace", "Beijing");
        passport = dbSession.save(passport);
        
        userAttr.setValue("height", 181);
        userAttr.setValue("passport", passport);
        userAttr = dbSession.save(userAttr);
        
        user.setValue("nickname", "Jeremy");
        user.setValue("attr", userAttr);
        user = dbSession.save(user);
        
        
        assertEquals((int)181, (int)user.v(dbSession,"attr.height"));
        
        String thePassportUuid = user.v(dbSession, "attr.passport");
        
        assertEquals(passport.getUuid(), thePassportUuid);

        assertEquals("GA12345678", user.v(dbSession,"attr.passport.number"));
        assertEquals("Beijing", user.v(dbSession,"attr.passport.issuePlace"));
    }
    
    @Test
    public void testReferenceMetaValue() {

        MetaEntity me = entityManager.getMetaEntity("user");
        
        MetaField mf = me.getMetaField("nickname");
        assertEquals(Consts.META_FIELD_TYPE_DATA_FIELD, mf.getFieldType());
        assertEquals(Consts.META_FIELD_DATA_TYPE_STRING, mf.getDataType());

        mf = me.getMetaField("attr");
        assertEquals(Consts.META_FIELD_TYPE_SINGLE_REFERENCE, mf.getFieldType());
        assertEquals("userAttr", mf.getTargetMetaEntity().getName());
        
        mf = me.getMetaField("attr.passport");
        assertEquals(Consts.META_FIELD_TYPE_SINGLE_REFERENCE, mf.getFieldType());
        assertEquals("passport", mf.getTargetMetaEntity().getName());

        mf = me.getMetaField("attr.passport.number");
        assertEquals(Consts.META_FIELD_TYPE_DATA_FIELD, mf.getFieldType());
        assertEquals(Consts.META_FIELD_DATA_TYPE_STRING, mf.getDataType());
        
    }
    
}
