package com.yotouch.core;

public interface ErrorCode {
    
    int USER_START                  = 10000;
    int NO_SUCH_USER                = USER_START + 1;
    int LOGIN_FAILED_WRONG_PASSWORD = USER_START + 101;

}
