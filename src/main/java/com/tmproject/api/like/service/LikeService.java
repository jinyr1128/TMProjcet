package com.tmproject.api.like.service;

import com.tmproject.api.board.entity.Board;
import com.tmproject.api.board.repository.BoardRepository;
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

    private LikeRepository likeRepository;
    private BoardRepository boardRepository;

    @Transactional
    public void saveBoardLike(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Like likeBoard = Like.likeBoard(member, board);

        likeRepository.save(likeBoard);
    }

    @Transactional
    public void unLikeComment(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Optional<Like> likeBoard = likeRepository.findByMemberAndBoard(member, board);

        if (likeBoard.isEmpty()) {
            throw new RuntimeException();
        }

        likeRepository.deleteById(likeBoard.get().getLikeId());
    }

    @Transactional
    public Optional<Like> findByMemberAndBoard(Member member, Board board) {
        return likeRepository.findByMemberAndBoard(member, board);
    }



}
