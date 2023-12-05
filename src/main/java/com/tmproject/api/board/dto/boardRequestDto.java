package com.tmproject.api.board.dto;

import com.tmproject.api.board.entity.Board;

public record boardRequestDto(
    String content
) {
    public Board toEntity(Board board) {
        return Board.builder()
            .content(content)
            .build();
    }
}
