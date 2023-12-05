package com.tmproject.api.comment.dto;


import com.tmproject.api.comment.entity.Comment;
import com.tmproject.global.common.ApiResponseDto;

public class CommentResponseDto extends ApiResponseDto {

    public CommentResponseDto(String message, int statusCode, Comment commentData) {
        super(message, statusCode, commentData);
    }
    public Comment getCommentData() {
        return (Comment) super.getData();
    }
}