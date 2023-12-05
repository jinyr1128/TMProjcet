package com.tmproject.api.like.controller;

import com.tmproject.api.like.service.LikeService;
import com.tmproject.global.common.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class likeController {

    private LikeService likeService;

    @PostMapping("/member/comments/{commentId}")
    public ResponseEntity<ApiResponseDto> likeComment(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long commentId
    ) {


        return null;
    }
}
