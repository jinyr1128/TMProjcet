package com.tmproject.api.board.service;

import com.tmproject.api.board.dto.BoardRequestDto;
import com.tmproject.api.board.dto.BoardResponseDto;
import com.tmproject.api.board.dto.BoardListDto;
import com.tmproject.api.board.entity.Board;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.board.repository.BoardRepository;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.api.board.exception.BoardNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    public BoardResponseDto createBoard(BoardRequestDto requestDto) {
        Member member = getCurrentMember();
        Board board = requestDto.toEntity();
        board.setMember(member);
        boardRepository.save(board);
        return new BoardResponseDto("게시글 작성 성공", 201, board);
    }

    public BoardListDto getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return new BoardListDto("전체 게시글 조회 성공", 200, boards);
    }

    public BoardResponseDto getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));
        return new BoardResponseDto("게시글 조회 성공", 200, board);
    }

    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));
        if (!board.getMember().getUsername().equals(getCurrentUsername())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        boardRepository.save(board);
        return new BoardResponseDto("게시글 수정 성공", 200, board);
    }

    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));
        if (!board.getMember().getUsername().equals(getCurrentUsername())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        boardRepository.delete(board);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Member getCurrentMember() {
        String username = getCurrentUsername();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}

