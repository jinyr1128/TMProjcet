package com.tmproject.api.member.service;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.member.dto.ProfileResponseDto;
import com.tmproject.api.member.dto.ProfileUpdateRequestDto;
import com.tmproject.api.member.dto.SignupRequestDto;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.entity.MemberRoleEnum;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.global.common.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private static Stack<String> passwordHistory = new Stack<>();
    @Test
    @DisplayName("[MemberService] signup() success")
    void signupSuccess() {
        // given
        SignupRequestDto requestDto = new SignupRequestDto("newUsername", "newPassword", "new@example.com");

        when(memberRepository.findByUsername("newUsername")).thenReturn(Optional.empty());
        when(memberRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // when
        ApiResponseDto<?> response = memberService.signup(requestDto);

        // then
        assertEquals("회원 가입 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());
        // response.getData()는 성공, 실패 여부 상관 없이 null이기에 검증 필요없음

        Mockito.verify(memberRepository, Mockito.times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("[MemberService] signup() duplicateUsername")
    void signupDuplicateUsername() {
        // given
        SignupRequestDto requestDto1 = new SignupRequestDto("duplicateUsername", "password1", "test1@naver.com");
        SignupRequestDto requestDto2 = new SignupRequestDto("duplicateUsername", "password2", "test2@naver.com");
        when(memberRepository.findByUsername(requestDto1.getUsername())).thenReturn(Optional.of(new Member()));

        // when
        ApiResponseDto<?> response1 = memberService.signup(requestDto1);
        ApiResponseDto<?> response2 = memberService.signup(requestDto2);

        // then
        assertEquals("중복된 사용자가 존재합니다.", response1.getMsg());
        assertEquals(400, response1.getStatusCode());
        assertEquals("중복된 사용자가 존재합니다.", response2.getMsg());
        assertEquals(400, response2.getStatusCode());
    }

    @Test
    @DisplayName("[MemberService] signup() duplicate email")
    void signupDuplicateEmail() {
        // given
        SignupRequestDto requestDto1 = new SignupRequestDto("test1", "password1", "duplicateEmail@naver.com");
        SignupRequestDto requestDto2 = new SignupRequestDto("test2", "password2", "duplicateEmail@naver.com");
        when(memberRepository.findByEmail(requestDto1.getEmail())).thenReturn(Optional.of(new Member()));

        // when
        ApiResponseDto<?> response1 = memberService.signup(requestDto1);
        ApiResponseDto<?> response2 = memberService.signup(requestDto2);

        // then
        assertEquals("중복된 Email이 존재합니다.", response1.getMsg());
        assertEquals(400, response1.getStatusCode());
        assertEquals("중복된 Email이 존재합니다.", response2.getMsg());
        assertEquals(400, response2.getStatusCode());
    }

    @Test
    @DisplayName("[MemberService] updateMember() success")
    void updateMemberSuccess() {
        // given
        long memberId = 1L;
        ProfileUpdateRequestDto requestDto = ProfileUpdateRequestDto
                .builder()
                .username("test1")
                .password("password")
                .passwordConfirm("password")
                .email("test1@naver.com")
                .build();

        Member memberEntity = Member
                .builder()
                .id(memberId)
                .username("test1")
                .password("password")
                .email("test3@naver.com")
                .build();

        MemberDetailsImpl memberDetails = MemberDetailsImpl
                .builder()
                .member(memberEntity)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(memberRepository.existsByEmailAndIdNot(requestDto.getEmail(), memberId)).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");

        // when
        ApiResponseDto<?> response = memberService.updateMember(memberId, requestDto, memberDetails);

        // then
        assertEquals("유저 프로필 수정 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    @DisplayName("[MemberService] updateMember changeUsername")
    void updateMemberChangeUsername() {
        // given
        long memberId = 1L;
        ProfileUpdateRequestDto requestDto = ProfileUpdateRequestDto
                .builder()
                .username("changeUsername")
                .password("password")
                .passwordConfirm("password")
                .email("test1@naver.com")
                .build();

        Member memberEntity = Member
                .builder()
                .id(memberId)
                .username("test1")
                .password("password")
                .build();

        MemberDetailsImpl memberDetails = MemberDetailsImpl
                .builder()
                .member(memberEntity)
                .build();
        // 해당 사용자는 username = "test" -> "changeUsername"로 변경
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));

        // when
        ApiResponseDto<?> response = memberService.updateMember(memberId, requestDto, memberDetails);

        // then
        assertEquals("유저 네임은 변경할 수 없습니다.", response.getMsg());
        assertEquals(400, response.getStatusCode());
    }

    @Test
    @DisplayName("[MemberService] updateMember() unauthorized member")
    void updateMemberUnauthorizatedMember() {
        // given
        long memberId = 2L;
        ProfileUpdateRequestDto requestDto = ProfileUpdateRequestDto
                .builder()
                .username("unauthorizatedMembername")
                .password("password")
                .passwordConfirm("password")
                .email("test1@naver.com")
                .build();

        Member adminEntity = Member
                .builder()
                .id(1L)
                .username("root")
                .password("1234")
                .build();
        // 실제 관리자

        Member unauthorizatedMember = Member.builder()
                .id(memberId)
                .username("unauthorizatedMembername")
                .password("password")
                .email("test1122@naver.com")
                .build();

        MemberDetailsImpl memberDetails = MemberDetailsImpl
                .builder()
                .member(unauthorizatedMember)
                .build();

        // findById(1L)이 호출되었을 때, adminEntity를 반환
        when(memberRepository.findById(1L)).thenReturn(Optional.of(adminEntity));

        // when
        ApiResponseDto<?> response = memberService.updateMember(adminEntity.getId(), requestDto, memberDetails);

        // then
        assertEquals("해당 사용자는 권한이 없습니다.", response.getMsg());
        assertEquals(403, response.getStatusCode());
    }

    @Test
    @DisplayName("[MemberService] updateMember() not equal RequestDto.getPasssword(), RequestDto.getPasswordConfirm ")
    void updateMemberNotEqualPasswordAndPasswordCofirm() {
        // given
        long memberId = 2L;
        ProfileUpdateRequestDto requestDto = ProfileUpdateRequestDto
                .builder()
                .username("test1")
                .password("password")
                .passwordConfirm("notEqualPassword")
                .email("test1@naver.com")
                .build();

        Member member = Member.builder()
                .id(memberId)
                .username("test1")
                .password("password")
                .email("test1@naver.com")
                .build();

        MemberDetailsImpl memberDetails = MemberDetailsImpl
                .builder()
                .member(member)
                .build();

        // findById(1L)이 호출되었을 때, adminEntity를 반환
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        ApiResponseDto<?> response = memberService.updateMember(memberId, requestDto, memberDetails);

        // then
        assertEquals("1차 비밀번호와 2차 비밀번호가 다릅니다.", response.getMsg());
        assertEquals(400, response.getStatusCode());
    }

    @Test
    @DisplayName("[MemberService] updateMember() exist email")
    void updateMemberExistsEmail() {
        // given
        long memberId = 2L;
        long member2Id = 3L;
        ProfileUpdateRequestDto requestDto = ProfileUpdateRequestDto
                .builder()
                .username("test2")
                .password("password")
                .passwordConfirm("password")
                .email("test1@naver.com")
                .build();

        Member member1 = Member.builder()
                .id(memberId)
                .username("test1")
                .password("password")
                .email("test1@naver.com")
                .build();

        Member member2 = Member.builder()
                .id(member2Id)
                .username("test2")
                .password("password")
                .email("test2@naver.com")
                .build();
        // duplicate Member Email

        MemberDetailsImpl memberDetails = MemberDetailsImpl
                .builder()
                .member(member2)
                .build();

        // findById(1L)이 호출되었을 때, adminEntity를 반환
        when(memberRepository.findById(member2Id)).thenReturn(Optional.of(member2));
        when(memberRepository.existsByEmailAndIdNot(requestDto.getEmail(), member2Id)).thenReturn(true);

        // when
        ApiResponseDto<?> response = memberService.updateMember(member2Id, requestDto, memberDetails);

        // then
        assertEquals("이미 사용 중인 이메일입니다.", response.getMsg());
        assertEquals(400, response.getStatusCode());
    }
    @Test
    @DisplayName("[MemberService] updateMember() password history restriction")
    void updateMemberPasswordHistoryRestriction() {
        // given
        long memberId = 2L;
        ProfileUpdateRequestDto requestDto1 = ProfileUpdateRequestDto
                .builder()
                .username("test1")
                .password("password")
                .passwordConfirm("password")
                .email("test1@naver.com")
                .build();

        ProfileUpdateRequestDto requestDto2 = ProfileUpdateRequestDto
                .builder()
                .username("test1")
                .password("password")
                .passwordConfirm("password")
                .email("test1@naver.com")
                .build();

        ProfileUpdateRequestDto requestDto3 = ProfileUpdateRequestDto
                .builder()
                .username("test1")
                .password("password")
                .passwordConfirm("password")
                .email("test1@naver.com")
                .build();

        ProfileUpdateRequestDto requestDto4 = ProfileUpdateRequestDto
                .builder()
                .username("test1")
                .password("password")
                .passwordConfirm("password")
                .email("test1@naver.com")
                .build();

        // 4번 수행해야 stack에 3번 쌓인 것을 통해서 확인 가능

        Member member = Member.builder()
                .id(memberId)
                .username("test1")
                .password("password")
                .email("test1@naver.com")
                .build();

        MemberDetailsImpl memberDetails = MemberDetailsImpl
                .builder()
                .member(member)
                .build();

        // Stubbing
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        passwordHistory.push(requestDto1.getPassword());
        passwordHistory.push(requestDto2.getPassword());
        passwordHistory.push(requestDto3.getPassword());
        passwordHistory.push(requestDto4.getPassword());

        // when
        ApiResponseDto<?> response1 = memberService.updateMember(memberId, requestDto1, memberDetails);
        ApiResponseDto<?> response2 = memberService.updateMember(memberId, requestDto2, memberDetails);
        ApiResponseDto<?> response3 = memberService.updateMember(memberId, requestDto3, memberDetails);
        ApiResponseDto<?> response4 = memberService.updateMember(memberId, requestDto4, memberDetails);
        // 똑같은 작업을 4번 실행해야 history에 쌓이면서 에러사항 충족

        // then
        assertEquals("유저 프로필 수정 성공", response1.getMsg());
        assertEquals(200, response1.getStatusCode());
        assertEquals("유저 프로필 수정 성공", response2.getMsg());
        assertEquals(200, response2.getStatusCode());
        assertEquals("유저 프로필 수정 성공", response3.getMsg());
        assertEquals(200, response3.getStatusCode());
        assertEquals("해당 비밀번호는 최근 3번 안에 사용한 비밀번호이기에 사용할 수 없습니다.", response4.getMsg());
        assertEquals(400, response4.getStatusCode());
    }

    private Member insertAdminData(){
        Member admin = Member.builder()
                .id(1L)
                .username("root")
                .password("adminPassword")
                .role(MemberRoleEnum.ADMIN)
                .build();
        return admin;
    }

    @Test
    @DisplayName("[MemberService] updateMemberProfileImage() success")
    void updateMemberProfileImageSuccess() {
        // given

        long memberId = 2L;
        MemberDetailsImpl memberDetails = new MemberDetailsImpl(
                Member.builder()
                    .id(memberId)
                    .username("test")
                    .password("password")
                    .role(MemberRoleEnum.USER)
                    .build()
        );

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());

        Member existingMember = Member.builder()
                .id(memberId)
                .username("test")
                .password("password")
                .role(MemberRoleEnum.USER)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

        // when
        ApiResponseDto<?> response = memberService.updateMemberProfileImage(memberId, imageFile, memberDetails);

        // then
        assertEquals("회원 프로필 사진 변경 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());

        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("[MemberService] updateMemberProfileImage() Not Found memberId")
    void updateMemberProfileImageNotFoundMemberId() {
        // given
        long memberId = 2L;
        long notFoundMemberId = 999L;

        MemberDetailsImpl memberDetails = new MemberDetailsImpl(
                Member.builder()
                        .id(memberId)
                        .username("test")
                        .password("password")
                        .role(MemberRoleEnum.USER)
                        .build()
        );

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());

        when(memberRepository.findById(notFoundMemberId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                memberService.updateMemberProfileImage(notFoundMemberId, imageFile, memberDetails)
        );

        assertEquals("해당 유저는 존재하지 않습니다.", exception.getMessage());

        verify(memberRepository, times(1)).findById(notFoundMemberId);
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("[MemberService] updateMemberProfileImage() Unauthorized Member")
    void updateMemberProfileImageUnauthorizedMember() {
        // given
        Member admin = insertAdminData();

        long memberId = 2L;

        Member unAuthorizedMember = Member.builder()
                .id(memberId)
                .username("test")
                .password("password")
                .role(MemberRoleEnum.USER)
                .build();

        MemberDetailsImpl memberDetails = new MemberDetailsImpl(unAuthorizedMember);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());

        when(memberRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        // when
        // @PostConstruct insertAdmin()을 통하여 이미 root 계정이 있음
        ApiResponseDto<?> response = memberService.updateMemberProfileImage(admin.getId(), imageFile, memberDetails);

        // then
        assertEquals("해당 사용자는 권한이 없습니다.", response.getMsg());
        assertEquals(403, response.getStatusCode());
    }

    @Test
    @DisplayName("[MemberService] updateMemberProfileImage() Unsupported file extension ")
    void updateMemberProfileImageUnsupportedFileExtension() {
        // given
        long memberId = 2L;

        Member member = Member.builder()
                .id(memberId)
                .username("test")
                .password("password")
                .role(MemberRoleEnum.USER)
                .build();

        MemberDetailsImpl memberDetails = new MemberDetailsImpl(member);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.pdf", "image/jpeg", "test image".getBytes());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        // @PostConstruct insertAdmin()을 통하여 이미 root 계정이 있음
        ApiResponseDto<?> response = memberService.updateMemberProfileImage(memberId, imageFile, memberDetails);

        // then
        assertEquals("지원하지 않는 이미지 확장자입니다.", response.getMsg());
        assertEquals(400, response.getStatusCode());
    }

    @Test
    @DisplayName("[MemberService] success")
    void getMemberInfoSuccess() {
        long memberId = 2L;

        Member member = Member.builder()
                .id(memberId)
                .username("test")
                .introduction("introduction")
                .profileImageUrl("uuid_image.jpeg")
                .role(MemberRoleEnum.USER)
                .build();

        MemberDetailsImpl memberDetails = new MemberDetailsImpl(member);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        ApiResponseDto<ProfileResponseDto> response = memberService.getMemberInfo(memberId, memberDetails);

        // then
        assertEquals("해당 유저 정보 조회 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());

        ProfileResponseDto profileResponseDto = response.getData();
        assertNotNull(profileResponseDto);
        assertEquals("test", profileResponseDto.getUsername());
        assertEquals("introduction", profileResponseDto.getIntroduction());

        assertEquals("해당 유저 정보 조회 성공", response.getMsg());
        assertEquals(200, response.getStatusCode());
    }


    @Test
    @DisplayName("[MemberService] Unauthorized Member access")
    void getMemberInfoUnauthorizedAccess() {
        // given

        long memberId = 2L;

        Member unauthorizedMember = Member.builder()
                .id(memberId)
                .username("test")
                .introduction("introduction")
                .profileImageUrl("uuid_image.jpeg")
                .role(MemberRoleEnum.USER)
                .build();

        MemberDetailsImpl memberDetails = new MemberDetailsImpl(unauthorizedMember);

        // when
        ApiResponseDto<ProfileResponseDto> response = memberService.getMemberInfo(1L, memberDetails);

        // then
        assertEquals("해당 유저는 접근 권한이 없습니다.", response.getMsg());
        assertEquals(403, response.getStatusCode());
        assertNull(response.getData());

    }
}
