package com.tmproject.api.like.dto;

import com.tmproject.api.comment.entity.Comment;
import com.tmproject.api.like.entity.Like;
import com.tmproject.api.member.entity.Member;

public record LikeComment(
    Long id,
    Member member,
    Comment comment
) {

    public LikeComment fromike(Like like) {
        return new LikeComment(
            like.getLikeId(),
            like.getMember(),
            like.getComment()
        );
    }
    public Like toEntity() {
        return Like.builder()
            .likeId(id)
            .member(member)
            .comment(comment)
            .build();
    }
}
