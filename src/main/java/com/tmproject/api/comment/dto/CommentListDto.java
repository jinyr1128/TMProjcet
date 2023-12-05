package com.tmproject.api.comment.dto;

import java.util.List;
import com.tmproject.api.comment.entity.Comment;

public class CommentListDto {
    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
