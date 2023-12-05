package com.tmproject.api.comment.controller;

import com.tmproject.api.comment.dto.CommentRequestDto;
import com.tmproject.api.comment.service.CommentService;
import com.tmproject.global.common.ApiResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponseDto> createComment(@PathVariable Long boardId, @RequestBody CommentRequestDto requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponseDto response = commentService.createComment(boardId, requestDto, username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponseDto> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponseDto response = commentService.updateComment(commentId, requestDto, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponseDto> deleteComment(@PathVariable Long commentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponseDto response = commentService.deleteComment(commentId, username);
        return ResponseEntity.ok(response);
    }
}