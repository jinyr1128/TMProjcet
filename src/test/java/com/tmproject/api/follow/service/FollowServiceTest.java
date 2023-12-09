package com.tmproject.api.follow.service;

import com.tmproject.api.follow.entity.Follow;
import com.tmproject.api.follow.repository.FollowRepository;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("팔로우 유저 테스트")
    public void testFollowUser() {
        Member member = new Member();
        Member follower = new Member();

        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(follower));
        when(followRepository.findByFollowingAndFollower(any(Member.class), any(Member.class))).thenReturn(Optional.empty());

        followService.followUser("username", 1L);

        verify(followRepository, times(1)).save(any(Follow.class));
    }

    @Test
    @DisplayName("언팔로우 유저 테스트")
    public void testUnFollowUser() {
        Member member = new Member();
        Member follower = new Member();
        Follow follow = new Follow(member, follower);

        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(follower));
        when(followRepository.findByFollowingAndFollower(any(Member.class), any(Member.class))).thenReturn(Optional.of(follow));

        followService.unFollowUser("username", 1L);

        verify(followRepository, times(1)).delete(any(Follow.class));
    }

    @Test
    @DisplayName("팔로우 중인 유저에 대한 테스트")
    public void testFollowUser_AlreadyFollowing() {
        Member member = new Member();
        Member follower = new Member();
        Follow follow = new Follow(member, follower);

        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(follower));
        when(followRepository.findByFollowingAndFollower(any(Member.class), any(Member.class))).thenReturn(Optional.of(follow));

        assertThrows(IllegalArgumentException.class, () -> {
            followService.followUser("username", 1L);
        });
    }

    @Test
    @DisplayName("팔로우하지 않은 유저에 대한 언팔로우 테스트")
    public void testUnFollowUser_NotFollowing() {
        Member member = new Member();
        Member follower = new Member();

        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(follower));
        when(followRepository.findByFollowingAndFollower(any(Member.class), any(Member.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            followService.unFollowUser("username", 1L);
        });
    }

}
