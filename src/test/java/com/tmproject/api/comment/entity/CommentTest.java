package com.tmproject.api.comment.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tmproject.api.member.entity.Member;
import com.tmproject.api.board.entity.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommentTest {

    @Test
    @DisplayName("댓글 엔티티 생성 및 필드 검증 테스트")
    public void createAndRetrieveComment() {
        // Given: 멤버 및 보드 엔티티 모의 생성
        Member member = new Member(); // 필요한 경우 멤버 필드 설정
        Board board = new Board(); // 필요한 경우 보드 필드 설정

        // When: 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .content("테스트 댓글 내용")
                .member(member)
                .board(board)
                .build();

        // Then: 댓글 엔티티 필드 검증
        assertNotNull(comment.getContent(), "댓글 내용이 null이면 안됩니다.");
        assertEquals("테스트 댓글 내용", comment.getContent(), "댓글 내용이 일치하지 않습니다.");
        assertEquals(member, comment.getMember(), "댓글 작성자가 일치하지 않습니다.");
        assertEquals(board, comment.getBoard(), "댓글이 달린 게시물이 일치하지 않습니다.");
    }
}