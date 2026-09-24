package com.reforgepc.exception;

public enum ErrorCode {

    EMAIL_ALREADY_REGISTERED(
        "EMAIL_ALREADY_REGISTERED",
        "This email is already registered."
    ),

    PASSWORD_MISMATCH(
        "PASSWORD_MISMATCH",
        "Passwords do not match."
    ),

    USER_NOT_FOUND(
        "USER_NOT_FOUND",
        "User was not found."
    );

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}