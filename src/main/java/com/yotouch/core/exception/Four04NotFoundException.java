package com.yotouch.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class Four04NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8720000495751396306L;

}
