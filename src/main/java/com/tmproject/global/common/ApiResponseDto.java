package com.tmproject.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null이 아닌 필드만 JSON에 포함
public class ApiResponseDto<T> {
    private String msg;
    private Integer statusCode;
    private T data; // 일반화된 데이터 필드

    public ApiResponseDto(String msg, Integer statusCode, T data) {
        this.msg = msg;
        this.statusCode = statusCode;
        this.data = data;
    }
}