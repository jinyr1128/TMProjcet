package com.tmproject.api.comment.controller;
import com.tmproject.api.comment.dto.CommentRequestDto;
import com.tmproject.api.comment.service.CommentService;
import com.tmproject.global.common.ApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentRequestDto requestDto;
    private ApiResponseDto expectedResponse;
    private String username = "testUser";

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        requestDto = new CommentRequestDto();
        expectedResponse = new ApiResponseDto();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void testCreateComment() {
        // Given
        setupMockSecurityContext(username);
        when(commentService.createComment(anyLong(), any(CommentRequestDto.class), anyString()))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<ApiResponseDto> response = commentController.createComment(1L, requestDto);

        // Then
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(commentService).createComment(1L, requestDto, username);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void testUpdateComment() {
        // Given
        setupMockSecurityContext(username);
        when(commentService.updateComment(anyLong(), any(CommentRequestDto.class), anyString()))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<ApiResponseDto> response = commentController.updateComment(1L, requestDto);

        // Then
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(commentService).updateComment(1L, requestDto, username);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void testDeleteComment() {
        // Given
        setupMockSecurityContext(username);
        when(commentService.deleteComment(anyLong(), anyString())).thenReturn(expectedResponse);

        // When
        ResponseEntity<ApiResponseDto> response = commentController.deleteComment(1L);

        // Then
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(commentService).deleteComment(1L, username);
        SecurityContextHolder.clearContext();
    }

    private void setupMockSecurityContext(String username) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
    }
}