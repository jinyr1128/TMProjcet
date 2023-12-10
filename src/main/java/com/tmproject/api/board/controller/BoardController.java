package com.tmproject.api.board.controller;


import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.board.dto.BoardListDto;
import com.tmproject.api.board.dto.BoardRequestDto;
import com.tmproject.api.board.dto.BoardResponseDto;
import com.tmproject.api.board.dto.BoardViewResponseDto;
import com.tmproject.api.board.service.BoardService;
import com.tmproject.api.member.entity.Member;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/member/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 게시물 작성
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
        @RequestBody BoardRequestDto boardRequestDto) {
        BoardResponseDto response = boardService.createBoard(boardRequestDto);
        return ResponseEntity.ok(response);
    }

    // 전체 게시물 조회
    @GetMapping
    public ResponseEntity<BoardListDto> getAllBoards() {
        BoardListDto response = boardService.getAllBoards();
        return ResponseEntity.ok(response);
    }

    // 게시물 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable Long boardId) {
        BoardResponseDto response = boardService.getBoard(boardId);
        return ResponseEntity.ok(response);
    }

    // 게시물 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long boardId,
        @RequestBody BoardRequestDto boardRequestDto) {
        BoardResponseDto response = boardService.updateBoard(boardId, boardRequestDto);
        return ResponseEntity.ok(response);
    }

    // 게시물 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(new BoardResponseDto("게시글 삭제성공", 200, null));
    }

    @GetMapping("/follow-true")
    public ResponseEntity<List<BoardViewResponseDto>> getFollowersBoards(
        @AuthenticationPrincipal MemberDetailsImpl memberDetails,
        @PageableDefault(sort = "createdAt") Pageable pageable
    ) {
        String myInfo = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println(myInfo);
        // 로그인을 했는데? 여기는 안들어가?
        // 여기가 java.lang.NullPointerException: Cannot invoke "com.tmproject.Common.Security.MemberDetailsImpl.getMember()"
        // because "memberDetails" is null
        // ? MemberDetails를 못받아 오는 이유가 뭐지?
        // 로그인한 사용자의 정보를 가져오는 건데...?
        // memberService에서는 잘 받아온단 말야? 뭔가 이상한데?
        log.info("memberDetails : "+ memberDetails);
        Member member = memberDetails.getMember();
        List<BoardViewResponseDto> BoardViewResponseDto = boardService.getFollowersBoards(member.getId(), pageable);

        return ResponseEntity.ok(BoardViewResponseDto);
    }

    @GetMapping("/like-true")
    public ResponseEntity<List<BoardViewResponseDto>> getLikeBoards(
        @AuthenticationPrincipal MemberDetailsImpl memberDetails,
        @PageableDefault(sort = "createdAt") Pageable pageable) {
        Member member = memberDetails.getMember();
        List<BoardViewResponseDto> BoardViewResponseDto = boardService.getlikeBoards(member.getId(), pageable);

        return ResponseEntity.ok(BoardViewResponseDto);
    }
}
