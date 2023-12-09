package com.tmproject.api.member.repository;

import com.tmproject.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String requestEmail, long memberId);
    Optional<Member> findByKakaoId(Long kakaoId);
    Optional<Member> findByNaverId(String naverId);
    Optional<Member> findByGoogleId(String googleId);
    Optional<Member> findByKakaoIdOrEmailOrUsername(Long kakaoId, String email, String username);
    Optional<Member> findByNaverIdOrEmailOrUsername(String naverId, String email, String username);
    Optional<Member> findByGoogleIdOrEmailOrUsername(String googleId, String email, String username);
}