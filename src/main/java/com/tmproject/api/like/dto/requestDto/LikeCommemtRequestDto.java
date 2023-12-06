package com.tmproject.api.like.dto.requestDto;

import com.tmproject.api.comment.entity.Comment;
import com.tmproject.api.like.entity.Like;
import com.tmproject.api.member.entity.Member;

public record LikeCommemtRequestDto(
    Comment comment
) {
    public Like toEntity(Member member) {
        return Like.builder()
            .member(member)
            .comment(comment)
            .build();
    }
}
