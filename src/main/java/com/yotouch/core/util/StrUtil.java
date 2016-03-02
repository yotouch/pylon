package com.yotouch.core.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class StrUtil {
    
    public String randStr() {
        return UUID.randomUUID().toString().toString().replace("-", "");
    }

}
