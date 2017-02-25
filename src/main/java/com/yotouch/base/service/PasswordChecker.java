package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;

public interface PasswordChecker {
    
    boolean checkPassword(Entity user, String passwd);
    
}
