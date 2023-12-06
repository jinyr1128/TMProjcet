package com.tmproject.api.follow.repository;

import com.tmproject.api.follow.entity.Follow;
import com.tmproject.api.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowingAndFollower(Member following, Member follower);
}