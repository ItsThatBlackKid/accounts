package com.saokanneh.auth.exception;

import java.io.Serial;

public class UserServiceException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -6269105285504236535L;

    public UserServiceException(String message) {
        super(message);
    }
}
