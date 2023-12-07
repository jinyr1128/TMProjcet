package com.tmproject.api.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.api.board.controller.BoardController;
import com.tmproject.api.board.dto.BoardListDto;
import com.tmproject.api.board.dto.BoardRequestDto;
import com.tmproject.api.board.dto.BoardResponseDto;
import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BoardControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private BoardService boardService;

    @InjectMocks
    private BoardController boardController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(boardController).build();
    }

    @Test
    @DisplayName("게시물 작성 통합 테스트")
    void createBoardTest() throws Exception {
        BoardRequestDto requestDto = new BoardRequestDto("Test Title", "Test Content");
        BoardResponseDto<Board> responseDto = new BoardResponseDto<>("게시글 작성 성공", 201, new Board());

        given(boardService.createBoard(requestDto)).willReturn(responseDto);

        mockMvc.perform(post("/api/member/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("게시글 작성 성공"));
    }
    @Test
    @DisplayName("전체 게시물 조회 통합 테스트")
    void getAllBoardsTest() throws Exception {
        BoardListDto boardListDto = new BoardListDto("전체 게시글 조회 성공", 200, List.of(new Board(), new Board()));

        given(boardService.getAllBoards()).willReturn(boardListDto);

        mockMvc.perform(get("/api/member/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("전체 게시글 조회 성공"))
                .andExpect(jsonPath("$.boards", hasSize(2)));
    }
    @Test
    @DisplayName("게시물 조회 통합 테스트")
    void getBoardTest() throws Exception {
        Board board = new Board();
        BoardResponseDto<Board> responseDto = new BoardResponseDto<>("게시글 조회 성공", 200, board);

        given(boardService.getBoard(1L)).willReturn(responseDto);

        mockMvc.perform(get("/api/member/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("게시글 조회 성공"));
    }
    @Test
    @DisplayName("게시물 수정 통합 테스트")
    void updateBoardTest() throws Exception {
        BoardRequestDto requestDto = new BoardRequestDto("Updated Title", "Updated Content");
        BoardResponseDto<Board> responseDto = new BoardResponseDto<>("게시글 수정 성공", 200, new Board());

        given(boardService.updateBoard(1L, requestDto)).willReturn(responseDto);

        mockMvc.perform(put("/api/member/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("게시글 수정 성공"));
    }
    @Test
    @DisplayName("게시물 삭제 통합 테스트")
    void deleteBoardTest() throws Exception {
        doNothing().when(boardService).deleteBoard(1L);

        mockMvc.perform(delete("/api/member/boards/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("")));
    }
}
