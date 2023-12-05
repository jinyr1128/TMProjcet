package com.tmproject.api.like.repository;

import com.tmproject.api.board.entity.Board;
import com.tmproject.api.comment.entity.Comment;
import com.tmproject.api.like.entity.Like;
import com.tmproject.api.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberAndBoard(Member member, Board board);
    Optional<Like> findByMemberAndComment(Member member, Comment comment);
}
