package com.tmproject.api.like.dto;

import com.tmproject.api.board.entity.Board;
import com.tmproject.api.like.entity.Like;
import com.tmproject.api.member.entity.Member;

public record LikeBoard(
    Long id,
    Member member,
    Board board

) {

    public LikeBoard fromLike(Like like) {
        return new LikeBoard(
            like.getLikeId(),
            like.getMember(),
            like.getBoard()
        );
    }

    public Like toEntity() {
        return Like.builder()
            .likeId(id)
            .member(member)
            .board(board)
            .build();
    }
}
