package com.tmproject.api.like.controller;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.like.service.LikeService;
import com.tmproject.api.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/member/boards/{boardId}/like")
    public ResponseEntity<?> likeBoard (
        @AuthenticationPrincipal MemberDetailsImpl memberDetails,
        @PathVariable Long boardId
    ) {
        Member member = memberDetails.getMember();
        likeService.saveBoardLike(boardId, member);
        return ResponseEntity.ok("게시글 좋아요 요청 성공");
    }

    @DeleteMapping("/member/boards/{boardId}/like")
    public ResponseEntity<?> unLikeBoard (
        @AuthenticationPrincipal MemberDetailsImpl memberDetails,
        @PathVariable Long boardId
    ) {
        Member member = memberDetails.getMember();
        likeService.unLikeBoard(boardId, member);
        return ResponseEntity.ok("게시글 좋아요 취소 성공");
    }

    @PostMapping("/member/comments/{commentId}/like")
    public ResponseEntity<?> likeComment (
        @AuthenticationPrincipal MemberDetailsImpl memberDetails,
        @PathVariable Long commentId
    ) {
        Member member = memberDetails.getMember();
        likeService.saveCommentLike(commentId, member);
        return ResponseEntity.ok("댓글 좋아요 요청 성공");
    }

    @DeleteMapping("/member/comments/{commentId}/like")
    public ResponseEntity<?> unLikeComment (
        @AuthenticationPrincipal MemberDetailsImpl memberDetails,
        @PathVariable Long commentId
    ) {
        Member member = memberDetails.getMember();
        likeService.unLikeComment(commentId, member);
        return ResponseEntity.ok("댓글 좋아요 취소 성공");
    }


}
