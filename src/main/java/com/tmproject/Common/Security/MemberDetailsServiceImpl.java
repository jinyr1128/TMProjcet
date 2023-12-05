package com.tmproject.Common.Security;

import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    public MemberDetailsServiceImpl(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException("해당 유저 네임을 찾을 수 없습니다.")
        );
        return new MemberDetailsImpl(member);
    }
}
