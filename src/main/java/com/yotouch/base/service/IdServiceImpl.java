package com.yotouch.base.service;

import org.springframework.stereotype.Service;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class IdServiceImpl implements IdService {

    Map<String, Object> syncKeys;

    @PostConstruct
    void init() {
        this.syncKeys = new HashMap<>();
    }

    @Override
    public int nextId(DbSession dbSession) {
        return this.nextSeqId(dbSession, "id");
    }

    @Override
    public int nextSeqId(DbSession dbSession, String name) {

        if (StringUtils.isEmpty(name)) {
            name = "DEFAULT_SEQ";
        }

        Object key = this.getSyncKey(name);

        synchronized (key) {
            Entity prop = dbSession.queryOneRawSql(
                    "prop",
                    "name = ?",
                    new Object[]{name}
            );

            int currentSeq = 0;
            if (prop == null) {
                prop = dbSession.newEntity("prop", Consts.STATUS_NORMAL);
                prop.setValue("name", name);
                prop = dbSession.save(prop);
            } else {
                currentSeq = Integer.parseInt(prop.v("value"));
            }

            currentSeq += 1;

            prop.setValue("value", currentSeq + "");
            dbSession.save(prop);

            return currentSeq;
        }
    }

    private Object getSyncKey(String name) {
        if (this.syncKeys.containsKey(name)) {
            return this.syncKeys.get(name);
        } else {
            this.syncKeys.put(name, new Object());
            return this.syncKeys.get(name);
        }
    }

}
