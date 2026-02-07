package com.gpfwzs.exception;

public class InviteCodeException extends RuntimeException {
    public InviteCodeException(String message) {
        super(message);
    }

    public InviteCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
