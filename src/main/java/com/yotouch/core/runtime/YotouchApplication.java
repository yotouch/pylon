package com.yotouch.core.runtime;

import com.yotouch.core.entity.EntityManager;

public interface YotouchApplication {

    YotouchRuntime getRuntime();
    
    EntityManager getEntityManager();

    void setAttribute(String key, Object value);

    Object getAttribute(String key);
}
