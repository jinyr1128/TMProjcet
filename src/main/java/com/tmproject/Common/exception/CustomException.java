package com.tmproject.Common.exception;

public class CustomException extends RuntimeException {

    private final int status;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
    }

    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.status = errorCode.getStatus();
    }
}
