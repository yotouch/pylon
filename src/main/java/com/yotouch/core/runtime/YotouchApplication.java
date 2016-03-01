package com.yotouch.core.runtime;

import com.yotouch.core.entity.EntityManager;

public interface YotouchApplication {

    YotouchRuntime getRuntime();
    
    EntityManager getEntityManager();
    
    Object getProp(String key);

    String getInstanceName();
    

}
