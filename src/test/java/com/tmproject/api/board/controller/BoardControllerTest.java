package com.tmproject.api.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.api.board.dto.BoardRequestDto;
import com.tmproject.api.board.dto.BoardResponseDto;
import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("게시물 작성 API 테스트")
    void createBoardTest() throws Exception {
        // Given: 테스트 데이터와 목 설정
        String title = "테스트 제목";
        String content = "테스트 내용";
        BoardRequestDto requestDto = new BoardRequestDto(title, content);
        BoardResponseDto responseDto = new BoardResponseDto("게시글 작성 성공", 201, new Board());
        given(boardService.createBoard(any(BoardRequestDto.class))).willReturn(responseDto);

        // When: API 요청 실행
        mockMvc.perform(post("/api/member/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // Then: 응답 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("게시글 작성 성공"));
    }

    @Test
    @DisplayName("특정 게시물 조회 API 테스트")
    void getBoardTest() throws Exception {
        // Given
        Long boardId = 1L;
        BoardResponseDto responseDto = new BoardResponseDto("게시글 조회 성공", 200, new Board());
        given(boardService.getBoard(boardId)).willReturn(responseDto);

        // When
        mockMvc.perform(get("/api/member/boards/" + boardId))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("게시글 조회 성공"));
    }

    @Test
    @DisplayName("게시물 수정 API 테스트")
    void updateBoardTest() throws Exception {
        // Given
        Long boardId = 1L;
        String updatedTitle = "수정된 제목";
        String updatedContent = "수정된 내용";
        BoardRequestDto requestDto = new BoardRequestDto(updatedTitle, updatedContent);
        BoardResponseDto responseDto = new BoardResponseDto("게시글 수정 성공", 200, new Board());
        given(boardService.updateBoard(eq(boardId), any(BoardRequestDto.class))).willReturn(responseDto);

        // When
        mockMvc.perform(put("/api/member/boards/" + boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("게시글 수정 성공"));
    }

    @Test
    @DisplayName("게시물 삭제 API 테스트")
    void deleteBoardTest() throws Exception {
        // Given
        Long boardId = 1L;
        willDoNothing().given(boardService).deleteBoard(boardId);

        // When
        mockMvc.perform(delete("/api/member/boards/" + boardId))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("게시글 삭제성공"));
    }
}
