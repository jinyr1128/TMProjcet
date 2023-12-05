package com.tmproject.api.like.service;

import com.tmproject.api.board.entity.Board;
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

    @Transactional
    public void saveBoardLike(Like like) {
        likeRepository.save(like);
    }

    @Transactional
    public void deleteById(Long id) {
        likeRepository.deleteById(id);
    }

    @Transactional
    public Optional<Like> findByMemberAndBoard(Member member, Board board) {
        return likeRepository.findByMemberAndBoard(member, board);
    }



}
