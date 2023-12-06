package com.tmproject.api.board.service;

import com.tmproject.api.board.dto.BoardListDto;
import com.tmproject.api.board.dto.BoardRequestDto;
import com.tmproject.api.board.dto.BoardResponseDto;
import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.exception.BoardNotFoundException;
import com.tmproject.api.board.repository.BoardRepository;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.entity.MemberRoleEnum;
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

    // 게시글 생성 성공 테스트
    @Test
    @DisplayName("게시글 생성 성공 테스트")
    void createBoard_Success() {
        // Given: Mock 객체와 상황 설정
        Member testMember = new Member("testUser", "password123", "test@example.com", MemberRoleEnum.USER);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(memberRepository.findByUsername("testUser")).thenReturn(Optional.of(testMember));
        when(boardRepository.save(any(Board.class))).thenAnswer(i -> i.getArguments()[0]);
        SecurityContextHolder.setContext(securityContext);

        BoardRequestDto requestDto = new BoardRequestDto("Title", "Content");

        // When: 테스트 대상 메서드 실행
        BoardResponseDto<Board> response = boardService.createBoard(requestDto);

        // Then: 결과 검증
        assertEquals("게시글 작성 성공", response.getMsg());
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getData());
    }

    // 전체 게시글 조회 성공 테스트
    @Test
    @DisplayName("전체 게시글 조회 성공 테스트")
    void getAllBoards_Success() {
        // Given
        List<Board> mockBoards = Arrays.asList(new Board(), new Board());
        when(boardRepository.findAll()).thenReturn(mockBoards);

        // When
        BoardListDto response = boardService.getAllBoards();

        // Then
        assertEquals("전체 게시글 조회 성공", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertEquals(2, response.getBoards().size());
    }

    // 게시글 조회 성공 테스트
    @Test
    @DisplayName("게시글 조회 성공 테스트")
    void getBoard_Success() {
        // Given
        Board mockBoard = new Board();
        when(boardRepository.findById(1L)).thenReturn(Optional.of(mockBoard));

        // When
        BoardResponseDto<Board> response = boardService.getBoard(1L);

        // Then
        assertEquals("게시글 조회 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
    }

    // 존재하지 않는 게시글 조회 시 예외 발생 테스트
    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생 테스트")
    void getBoard_NotFound() {
        // Given
        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then: 예외 발생 검증
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.getBoard(1L);
        });
    }

    // 게시글 수정 성공 테스트
    @Test
    @DisplayName("게시글 수정 성공 테스트")
    void updateBoard_Success() {
        // Given
        Member mockMember = new Member("testUser", "password123", "test@example.com", MemberRoleEnum.USER);
        Board mockBoard = new Board();
        mockBoard.setMember(mockMember);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(mockBoard));
        when(boardRepository.save(any(Board.class))).thenAnswer(i -> i.getArguments()[0]);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);
        BoardRequestDto requestDto = new BoardRequestDto("Updated Title", "Updated Content");

        // When
        BoardResponseDto<Board> response = boardService.updateBoard(1L, requestDto);

        // Then
        assertEquals("게시글 수정 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals("Updated Title", response.getData().getTitle());
        assertEquals("Updated Content", response.getData().getContent());
    }

    // 게시글 삭제 성공 테스트
    @Test
    @DisplayName("게시글 삭제 성공 테스트")
    void deleteBoard_Success() {
        // Given
        Member mockMember = new Member("testUser", "password123", "test@example.com", MemberRoleEnum.USER);
        Board mockBoard = new Board();
        mockBoard.setMember(mockMember);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(mockBoard));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        // When & Then: 예외 발생이 없는지 검증
        assertDoesNotThrow(() -> boardService.deleteBoard(1L));
    }

    // 존재하지 않는 게시글 삭제 시 예외 발생 테스트
    @Test
    @DisplayName("존재하지 않는 게시글 삭제 시 예외 발생 테스트")
    void deleteBoard_NotFound() {
        // Given
        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then: 예외 발생 검증
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.deleteBoard(1L);
        });
    }
}