package com.yotouch.core.config;

import java.io.File;

public interface Configure {
    
    File getRuntimeHome();
    
    File getEtcDir();

    Object getProp(String key);

}
