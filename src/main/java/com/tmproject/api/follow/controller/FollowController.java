package com.tmproject.api.follow.controller;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{followerId}/follow")
    public ResponseEntity<?> followUser(
        @AuthenticationPrincipal MemberDetailsImpl memberDetails,
        @PathVariable Long followerId) {

        String username = memberDetails.getUsername();

        followService.followUser(username, followerId);
        return ResponseEntity.status(HttpStatus.OK).body("요청 성공");
    }

    @DeleteMapping("/{followerId}/follow")
    public ResponseEntity<?> unFollowUser(
        @AuthenticationPrincipal MemberDetailsImpl memberDetails,
        @PathVariable Long followerId) {
        String username = memberDetails.getUsername();

        followService.unFollowUser(username, followerId);
        return ResponseEntity.status(HttpStatus.OK).body("요청 성공");
    }
}
