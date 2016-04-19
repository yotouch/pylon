package com.yotouch.core;

public interface ErrorCode {
    
    int USER_START                  = 10000;
    int NO_SUCH_USER                = USER_START + 1;
    int LOGIN_FAILED_WRONG_PASSWORD = USER_START + 101;
    int USER_EXISTS                 = USER_START + 102;
    int VALID_SMS_CODE_VALID        = USER_START + 103;

}
