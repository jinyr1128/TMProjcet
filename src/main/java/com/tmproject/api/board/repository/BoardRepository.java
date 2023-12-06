package com.tmproject.api.board.repository;

import com.tmproject.api.board.entity.Board;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = "member")
    @Query(
        "select b from Board b where b.member in " +
            "(select f.follower from Follow f where f.following.id = :memberId)"
    )
    Page<Board> findAllUserFollowerBoard(@Param("memberId") Long memberId, Pageable pageable);

}

