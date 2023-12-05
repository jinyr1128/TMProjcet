package com.tmproject.api.comment.service;

import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.repository.BoardRepository;
import com.tmproject.api.comment.dto.CommentRequestDto;
import com.tmproject.api.comment.dto.CommentResponseDto;
import com.tmproject.api.comment.entity.Comment;
import com.tmproject.api.comment.exception.CommentNotFoundException;
import com.tmproject.api.comment.repository.CommentRepository;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.global.common.ApiResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    public ApiResponseDto createComment(Long boardId, CommentRequestDto requestDto, String username) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException("Board not found"));
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Member not found"));

        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .member(member)
                .board(board)
                .build();

        commentRepository.save(comment);
        return new CommentResponseDto("댓글 작성 성공", 200, comment);
    }

    public ApiResponseDto updateComment(Long commentId, CommentRequestDto requestDto, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        if (!comment.getMember().getUsername().equals(username)) {
            throw new IllegalArgumentException("No permission to update this comment");
        }

        comment.setContent(requestDto.getContent());
        commentRepository.save(comment);
        return new CommentResponseDto("댓글 수정 성공", 200, comment);
    }

    public ApiResponseDto deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        if (!comment.getMember().getUsername().equals(username)) {
            throw new IllegalArgumentException("No permission to delete this comment");
        }

        commentRepository.delete(comment);
        return new CommentResponseDto("댓글 삭제 성공", 200, null);
    }
}