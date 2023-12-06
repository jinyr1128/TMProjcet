package com.tmproject.api.board.dto;

import com.tmproject.api.board.entity.Board;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BoardViewResponseDto(

    String userName,
    Long boardId,
    String boardTitle,
    String boardContent,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
) {

    public static BoardViewResponseDto from(Board board) {
        return BoardViewResponseDto.builder()
            .userName(board.getMember().getUsername())
            .boardId(board.getId())
            .boardTitle(board.getTitle())
            .boardContent(board.getContent())
            .createdDate(board.getCreatedAt())
            .modifiedDate(board.getModifiedAt())
            .build();
    }
}
