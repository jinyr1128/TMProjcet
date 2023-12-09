package com.tmproject.api.like.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.tmproject.api.board.entity.Board;
import com.tmproject.api.comment.entity.Comment;
import com.tmproject.api.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LikeTest {
    @Test
    @DisplayName("likeBoard 빌더 테스트")
    public void testLikeBoard() {
        // Given
        Member member = mock(Member.class);
        Board board = mock(Board.class);

        // When
        Like like = Like.likeBoard(member, board);

        // Then
        assertNotNull(like);
        assertEquals(member, like.getMember());
        assertEquals(board, like.getBoard());
    }

    @Test
    @DisplayName("likeComment 빌더 테스트")
    public void testLikeComment() {
        // Given
        Member member = mock(Member.class);
        Comment comment = mock(Comment.class);

        // When
        Like like = Like.likeComment(member, comment);

        // Then
        assertNotNull(like);
        assertEquals(member, like.getMember());
        assertEquals(comment, like.getComment());
    }
}