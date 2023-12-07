package com.tmproject.api.board.entity;

import com.tmproject.api.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        // Board 인스턴스 초기화
        board = new Board();
    }

    @Test
    @DisplayName("게시물 생성 테스트")
    void createBoard() {
        // 게시글 생성 및 기본 필드 설정
        Long expectedId = 1L;
        String expectedTitle = "Test Title";
        String expectedContent = "Test Content";
        Member expectedMember = new Member(); // Member 인스턴스 초기화 필요

        board.setId(expectedId);
        board.setTitle(expectedTitle);
        board.setContent(expectedContent);
        board.setMember(expectedMember);

        // 검증
        assertEquals(expectedId, board.getId());
        assertEquals(expectedTitle, board.getTitle());
        assertEquals(expectedContent, board.getContent());
        assertEquals(expectedMember, board.getMember());
    }

    @Test
    @DisplayName("게시물 수정 테스트")
    void updateBoardProperties() {
        // 게시글 속성 업데이트
        String newTitle = "New Title";
        String newContent = "New Content";

        board.setTitle(newTitle);
        board.setContent(newContent);

        // 검증
        assertEquals(newTitle, board.getTitle());
        assertEquals(newContent, board.getContent());
    }
}
