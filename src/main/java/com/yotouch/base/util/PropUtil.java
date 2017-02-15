package com.yotouch.base.util;

import javax.annotation.PostConstruct;

import com.yotouch.core.Consts;
import org.springframework.stereotype.Component;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.springframework.util.StringUtils;

@Component
public class PropUtil {
    
    
    @PostConstruct
    public void initDefaultValue() {
                
    }

    public Entity getOrCreateProp(DbSession dbSession, String name) {
        Entity prop = this.getProp(dbSession, name);
        if (prop == null) {
            prop = dbSession.newEntity("prop", Consts.STATUS_NORMAL);
            prop.setValue("name", name);
        }
        return prop;
    }
    
    public Entity getProp(DbSession dbSession, String name) {
        Entity entity = dbSession.queryOneRawSql("prop", "name=?", new Object[]{name});
        return entity;
    }
    
    public String getPropValue(DbSession dbSession, String name) {
        Entity entity = getProp(dbSession, name);
        if (entity == null) {
            return null;
        }
        
        return entity.v("value");
    }

    public synchronized int nextSeq(DbSession dbSession, String name) {
        return this.nextSeq(dbSession, name, 1);
    }

    public synchronized int nextSeq(DbSession dbSession, String name, int step) {
        Entity prop = this.getProp(dbSession, name);
        int seq = 0;

        if (prop == null) {
            prop = dbSession.newEntity("prop");
            prop.setValue("name", name);
            prop.setValue("value", step);
            prop = dbSession.save(prop);
        }

        prop = dbSession.increase(prop, "value", step);

        String seqStr = prop.v("value");
        if (!StringUtils.isEmpty(seqStr)) {
            try {
                seq = Integer.parseInt(seqStr);
            } catch (Exception e) {
            }
        }

        return seq;
    }


    public Entity setProp(DbSession dbSession, String name, String value) {
        Entity prop = this.getOrCreateProp(dbSession, name);
        prop.setValue("value", value);
        return dbSession.save(prop);
    }
}
