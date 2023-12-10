package com.tmproject.api.like.service;

import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.repository.BoardRepository;
import com.tmproject.api.comment.entity.Comment;
import com.tmproject.api.comment.repository.CommentRepository;
import com.tmproject.api.like.dto.LikeBoard;
import com.tmproject.api.like.entity.Like;
import com.tmproject.api.like.repository.LikeRepository;
import com.tmproject.api.member.entity.Member;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void saveBoardLike(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Like likeBoard = Like.likeBoard(member, board);
        likeRepository.save(likeBoard);
    }

    @Transactional
    public void unLikeBoard(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Optional<Like> likeBoard = likeRepository.findByMemberAndBoard(member, board);

        if (likeBoard.isEmpty()) {
            throw new RuntimeException();
        }

        likeRepository.deleteById(likeBoard.get().getLikeId());
    }

    @Transactional
    public void saveCommentLike(Long boardId, Member member) {
        Comment comment = commentRepository.findById(boardId).orElseThrow();
        Like likeBoard = Like.likeComment(member, comment);

        likeRepository.save(likeBoard);
    }

    @Transactional
    public void unLikeComment(Long boardId, Member member) {
        Comment comment = commentRepository.findById(boardId).orElseThrow();
        Optional<Like> likeComment = likeRepository.findByMemberAndComment(member, comment);

        if (likeComment.isEmpty()) {
            throw new RuntimeException();
        }

        likeRepository.deleteById(likeComment.get().getLikeId());
    }
}
