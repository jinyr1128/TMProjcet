package com.tmproject.api.comment.service;

import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.repository.BoardRepository;
import com.tmproject.api.comment.dto.CommentRequestDto;
import com.tmproject.api.comment.entity.Comment;
import com.tmproject.api.comment.repository.CommentRepository;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.entity.MemberRoleEnum;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.global.common.ApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private CommentService commentService;

    private Member member;
    private Board board;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Member 객체 초기화
        member = mock(Member.class);
        when(member.getId()).thenReturn(1L);
        when(member.getUsername()).thenReturn("testuser");
        when(member.getPassword()).thenReturn("testpass123");
        when(member.getEmail()).thenReturn("testuser@example.com");
        when(member.getProfileImageUrl()).thenReturn("http://example.com/profile.jpg");
        when(member.getIntroduction()).thenReturn("Hello, I'm a test user");
        when(member.getRole()).thenReturn(MemberRoleEnum.USER);

        // Board 객체 초기화
        board = mock(Board.class);
        when(board.getId()).thenReturn(1L);
        when(board.getTitle()).thenReturn("Test Board Title");
        when(board.getContent()).thenReturn("This is a test board content");
        when(board.getMember()).thenReturn(member);

        // Comment 객체 초기화
        comment = mock(Comment.class);
        when(comment.getId()).thenReturn(1L);
        when(comment.getContent()).thenReturn("Initial comment content");
        when(comment.getMember()).thenReturn(member);
        when(comment.getBoard()).thenReturn(board);
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createCommentTest() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("댓글 내용");
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
        when(commentRepository.save(ArgumentMatchers.any(Comment.class))).thenReturn(comment);

        // when
        ApiResponseDto response = commentService.createComment(1L, requestDto, "user1");

        // then
        assertNotNull(response);
        assertEquals("댓글 작성 성공", response.getMsg());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateCommentTest() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("수정된 댓글 내용");
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(comment.getMember()).thenReturn(member);
        when(member.getUsername()).thenReturn("user1");

        // when
        ApiResponseDto response = commentService.updateComment(1L, requestDto, "user1");

        // then
        assertNotNull(response);
        assertEquals("댓글 수정 성공", response.getMsg());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteCommentTest() {
        // given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(comment.getMember()).thenReturn(member);
        when(member.getUsername()).thenReturn("user1");

        // when
        ApiResponseDto response = commentService.deleteComment(1L, "user1");

        // then
        assertNotNull(response);
        assertEquals("댓글 삭제 성공", response.getMsg());
    }
}

