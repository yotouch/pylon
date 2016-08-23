package com.yotouch.core.runtime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YotouchRuntimeImpl implements YotouchRuntime {

    @Autowired
    private DbSession dbSession;

    @Override
    public DbSession createDbSession() {
        return this.dbSession;
    }


}
