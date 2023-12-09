package com.tmproject.api.like.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.like.service.LikeService;
import com.tmproject.api.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

class likeControllerTest {

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("게시글 좋아요 테스트")
    public void testLikeBoard() {
        //Given
        MemberDetailsImpl memberDetails = mock(MemberDetailsImpl.class);
        Member member = mock(Member.class);
        Long boardId = 1L;

        when(memberDetails.getMember()).thenReturn(member);

        // When
        ResponseEntity<?> response = likeController.likeBoard(memberDetails, boardId);

        // Then
        verify(likeService, times(1)).saveBoardLike(boardId, member);
        assertEquals(ResponseEntity.ok("게시글 좋아요 요청 성공"), response);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 테스트")

    public void testUnLikeBoard() {
        // Given
        MemberDetailsImpl memberDetails = mock(MemberDetailsImpl.class);
        Member member = mock(Member.class);
        Long boardId = 1L;

        when(memberDetails.getMember()).thenReturn(member);

        // When
        ResponseEntity<?> response = likeController.unLikeBoard(memberDetails, boardId);

        // Then
        verify(likeService, times(1)).unLikeBoard(boardId, member);
        assertEquals(ResponseEntity.ok("게시글 좋아요 취소 성공"), response);
    }

    @Test
    @DisplayName("댓글 좋아요 테스트")
    public void testLikeComment() {
        // Given
        MemberDetailsImpl memberDetails = mock(MemberDetailsImpl.class);
        Member member = mock(Member.class);
        Long commentId = 1L;

        when(memberDetails.getMember()).thenReturn(member);

        // When
        ResponseEntity<?> response = likeController.likeComment(memberDetails, commentId);

        // Then
        verify(likeService, times(1)).saveCommentLike(commentId, member);
        assertEquals(ResponseEntity.ok("댓글 좋아요 요청 성공"), response);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 테스트")
    public void testUnLikeComment() {
        // Given
        MemberDetailsImpl memberDetails = mock(MemberDetailsImpl.class);
        Member member = mock(Member.class);
        Long commentId = 1L;

        when(memberDetails.getMember()).thenReturn(member);

        // When
        ResponseEntity<?> response = likeController.unLikeComment(memberDetails, commentId);

        // Then
        verify(likeService, times(1)).unLikeComment(commentId, member);
        assertEquals(ResponseEntity.ok("댓글 좋아요 취소 성공"), response);
    }


}