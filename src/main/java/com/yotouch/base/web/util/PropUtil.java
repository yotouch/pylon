package com.yotouch.base.web.util;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

@Component
public class PropUtil {
    
    
    @PostConstruct
    public void initDefaultValue() {
                
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

    public int nextSeq(DbSession dbSession, String name) {
        Entity prop = this.getProp(dbSession, name);
        int seq = Integer.parseInt(prop.v("value"));
        
        seq += 1;
        
        prop.setValue("value", seq);
        dbSession.save(prop);
        
        return seq;
    }

        

}
