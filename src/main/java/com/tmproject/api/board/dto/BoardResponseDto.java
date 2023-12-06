package com.tmproject.api.board.dto;

import com.tmproject.global.common.ApiResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardResponseDto<T> extends ApiResponseDto<T> {

    public BoardResponseDto(String msg, Integer statusCode, T data) {
        super(msg, statusCode, data);
    }

}
