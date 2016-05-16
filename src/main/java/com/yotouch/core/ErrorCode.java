package com.yotouch.core;

public interface ErrorCode {
    
    int USER_START                  = 10000;
    int NO_SUCH_USER                = USER_START + 1;
    int LOGIN_FAILED_WRONG_PASSWORD = USER_START + 101;
    int USER_EXISTS                 = USER_START + 102;
    int PASSWORD_NOT_SAME           = USER_START + 103;


    int VALID_SMS_CODE_VALID        = USER_START + 103;

    int INPUT_START                 = 20000;
    int INPUT_ERROR                 = INPUT_START + 201;
}
