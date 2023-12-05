package com.tmproject.api.board.dto;

import com.tmproject.api.board.entity.Board;
public record BoardRequestDto(String title, String content) {
    public Board toEntity() {
        return Board.builder()
                .title(title)
                .content(content)
                .build();
    }
}