package com.tmproject.api.follow.service;

import com.tmproject.api.follow.entity.Follow;
import com.tmproject.api.follow.repository.FollowRepository;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    public void followUser(String username, Long followerId) {
        Member member = checkMember(username);
        Member follower = checkFollower(followerId);

        if (isAlreadyFollow(member, follower)) {
            throw new IllegalArgumentException();
        }

        followRepository.save(new Follow(member, follower));
    }

    public void unFollowUser(String username, Long followerId) {
        Member user = checkMember(username);
        Member follower = checkFollower(followerId);

        if (!isAlreadyFollow(user, follower)) {
            throw new IllegalArgumentException();
        }
        Optional<Follow> follow = followRepository.findByFollowingAndFollower(user, follower);

        follow.ifPresent(followRepository::delete);
    }

    private Member checkMember(String username) {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return member.get();
    }

    private Member checkFollower(Long followerId) {
        Optional<Member> follower = memberRepository.findById(followerId);
        if (follower.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return follower.get();
    }

    private boolean isAlreadyFollow(Member following, Member follower) {
        Optional<Follow> optionalFollow = followRepository.findByFollowingAndFollower(following,
            follower);
        return optionalFollow.isPresent();
    }


}