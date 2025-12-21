package com.ktpm.potatoapi.common.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode baseErrorCode) {
        super(baseErrorCode.getMessage());
        this.errorCode = baseErrorCode;
    }
}
