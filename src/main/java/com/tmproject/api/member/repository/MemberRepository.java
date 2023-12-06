package com.tmproject.api.member.repository;

import com.tmproject.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String requestEmail, long memberId);
    boolean existsByUsernameAndIdNot(String username, long memberId);
    Optional<Member> findByUsernameOrEmail(String usernameOrEmail, String usernameOrEmail1);
}