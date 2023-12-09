package com.tmproject.api.like.service;

import static org.junit.jupiter.api.Assertions.*;

import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.repository.BoardRepository;
import com.tmproject.api.comment.entity.Comment;
import com.tmproject.api.comment.repository.CommentRepository;
import com.tmproject.api.like.entity.Like;
import com.tmproject.api.like.repository.LikeRepository;
import com.tmproject.api.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        likeService = new LikeService(likeRepository, boardRepository, commentRepository);
    }

    @Test
    @DisplayName("게시글 좋아요 서비스 테스트")
    public void testSaveBoardLike() {
        // Given
        Long boardId = 1L;
        Member member = mock(Member.class);
        Board board = mock(Board.class);
        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // When
        likeService.saveBoardLike(boardId, member);

        // Then
        verify(likeRepository, times(1)).save(likeCaptor.capture());
        Like savedLike = likeCaptor.getValue();
        assertNotNull(savedLike);
        assertEquals(member, savedLike.getMember());
        assertEquals(board, savedLike.getBoard());
    }

    @Test
    @DisplayName("게시글 좋아요 취소 서비스 테스트")
    public void testUnLikeBoard() {
        // Given
        Long boardId = 1L;
        Member member = mock(Member.class);
        Board board = mock(Board.class);
        Like likeBoard = mock(Like.class);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(likeRepository.findByMemberAndBoard(member, board)).thenReturn(Optional.of(likeBoard));

        // When
        likeService.unLikeBoard(boardId, member);

        // Then
        verify(likeRepository, times(1)).deleteById(likeBoard.getLikeId());
    }

    @Test
    @DisplayName("댓글 좋아요 서비스 테스트")
    public void testSaveCommentLike() {
        // Given
        Long commentId = 1L;
        Member member = mock(Member.class);
        Comment comment = mock(Comment.class);
        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // When
        likeService.saveCommentLike(commentId, member);

        // Then
        verify(likeRepository, times(1)).save(likeCaptor.capture());
        Like savedLike = likeCaptor.getValue();
        assertNotNull(savedLike);
        assertEquals(member, savedLike.getMember());
        assertEquals(comment, savedLike.getComment());
    }

    @Test
    @DisplayName("댓글 좋아요 취소 서비스 테스트")
    public void testUnLikeComment() {
        // Given
        Long commentId = 1L;
        Member member = mock(Member.class);
        Comment comment = mock(Comment.class);
        Like likeComment = mock(Like.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByMemberAndComment(member, comment)).thenReturn(Optional.of(likeComment));

        // When
        likeService.unLikeComment(commentId, member);

        // Then
        verify(likeRepository, times(1)).deleteById(likeComment.getLikeId());
    }
}