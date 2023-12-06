package com.tmproject.api.board.service;

import com.tmproject.api.board.dto.BoardListDto;
import com.tmproject.api.board.dto.BoardRequestDto;
import com.tmproject.api.board.dto.BoardResponseDto;
import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.exception.BoardNotFoundException;
import com.tmproject.api.board.repository.BoardRepository;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;


    @Test
    @DisplayName("게시글 생성 성공 테스트")
    void createBoard_Success() {
        // Mocking
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(memberRepository.findByUsername("testUser")).thenReturn(Optional.of(new Member()));
        when(boardRepository.save(any(Board.class))).thenAnswer(i -> i.getArguments()[0]);

        SecurityContextHolder.setContext(securityContext);

        BoardRequestDto requestDto = new BoardRequestDto("Title", "Content");
        BoardResponseDto<Board> response = boardService.createBoard(requestDto);

        assertEquals("게시글 작성 성공", response.getMsg());
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getData());
    }
    @Test
    @DisplayName("전체 게시글 조회 성공 테스트")
    void getAllBoards_Success() {
        List<Board> mockBoards = Arrays.asList(new Board(), new Board());
        when(boardRepository.findAll()).thenReturn(mockBoards);

        BoardListDto response = boardService.getAllBoards();

        assertEquals("전체 게시글 조회 성공", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertEquals(2, response.getBoards().size());
    }
    @Test
    @DisplayName("게시글 조회 성공 테스트")
    void getBoard_Success() {
        Board mockBoard = new Board();
        when(boardRepository.findById(1L)).thenReturn(Optional.of(mockBoard));

        BoardResponseDto<Board> response = boardService.getBoard(1L);



        assertEquals("게시글 조회 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생 테스트")
    void getBoard_NotFound() {
        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> {
            boardService.getBoard(1L);
        });
    }
    @Test
    @DisplayName("게시글 수정 성공 테스트")
    void updateBoard_Success() {
        // Member 객체 생성
        Member mockMember = new Member("testUser", "password123", "test@example.com");

        // Board 객체에 Member 설정
        Board mockBoard = new Board();
        mockBoard.setMember(mockMember);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(mockBoard));
        when(boardRepository.save(any(Board.class))).thenAnswer(i -> i.getArguments()[0]);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        SecurityContextHolder.setContext(securityContext);

        BoardRequestDto requestDto = new BoardRequestDto("Updated Title", "Updated Content");
        BoardResponseDto<Board> response = boardService.updateBoard(1L, requestDto);

        assertEquals("게시글 수정 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals("Updated Title", response.getData().getTitle());
        assertEquals("Updated Content", response.getData().getContent());
    }
    @Test
    @DisplayName("게시글 삭제 성공 테스트")
    void deleteBoard_Success() {
        // Member 객체 생성
        Member mockMember = new Member("testUser", "password123", "test@example.com");

        // Board 객체에 Member 설정
        Board mockBoard = new Board();
        mockBoard.setMember(mockMember);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(mockBoard));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        SecurityContextHolder.setContext(securityContext);

        assertDoesNotThrow(() -> boardService.deleteBoard(1L));
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 시 예외 발생 테스트")
    void deleteBoard_NotFound() {
        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> {
            boardService.deleteBoard(1L);
        });
    }

}
