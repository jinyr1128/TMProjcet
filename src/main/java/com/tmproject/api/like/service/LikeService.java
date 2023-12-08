package com.tmproject.api.like.service;

import com.tmproject.Common.exception.CustomException;
import com.tmproject.Common.exception.ErrorCode;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void saveBoardLike(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow();

        if (likeRepository.findByMemberAndBoard(member, board).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_LIKE_EXCEPTION);
        }

        Like likeBoard = Like.likeBoard(member, board);
        likeRepository.save(likeBoard);
    }

    @Transactional
    public void unLikeBoard(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Optional<Like> likeBoard = likeRepository.findByMemberAndBoard(member, board);

        if (likeBoard.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_LIKE_EXCEPTION);
        }

        likeRepository.deleteById(likeBoard.get().getLikeId());
    }

    @Transactional
    public void saveCommentLike(Long boardId, Member member) {
        Comment comment = commentRepository.findById(boardId).orElseThrow();

        if (likeRepository.findByMemberAndComment(member, comment).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_LIKE_EXCEPTION);
        }

        Like LikeComment = Like.likeComment(member, comment);
        likeRepository.save(LikeComment);
    }

    @Transactional
    public void unLikeComment(Long boardId, Member member) {
        Comment comment = commentRepository.findById(boardId).orElseThrow();
        Optional<Like> likeComment = likeRepository.findByMemberAndComment(member, comment);

        if (likeComment.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_LIKE_EXCEPTION);
        }

        likeRepository.deleteById(likeComment.get().getLikeId());
    }

//    @Transactional
//    public int getBoardLikeCount(Member member, Long boardId) {
//        Optional<Like> likeBoards = likeRepository.findAllByMember()
//    }





}
