package com.tmproject.Common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 팔로우
    NOT_FOUND_FOLLOW_EXCEPTION(401, "팔로우 내역을 찾을 수 없습니다."),
    DUPLICATED_FOLLOW_EXCEPTION(401, "팔로우 내역이 이미 존재합니다."),

    // 좋아요
    NOT_FOUND_LIKE_EXCEPTION(401, "좋아요 내역을 찾을 수 없습니다."),
    DUPLICATED_LIKE_EXCEPTION(401, "좋아요 내역이 이미 존재합니다.")
    ;

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
