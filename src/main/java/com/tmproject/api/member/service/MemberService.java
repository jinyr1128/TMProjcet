package com.tmproject.api.member.service;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.member.dto.ProfileResponseDto;
import com.tmproject.api.member.dto.ProfileUpdateRequestDto;
import com.tmproject.api.member.dto.SignupRequestDto;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.entity.MemberRoleEnum;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.global.common.ApiResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminName;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${file.path}")
    private String uploadFolder;
    // cloud 굳이 쓸 필요 없다
    // cloud 시간 남으면 해보자

    @PostConstruct
    public void createAdminAccount(){
        // Admin 계정
        if (!memberRepository.existsByUsername(adminName)) {
            // admin계정은 중복되지 않는다는 가정, 추후 signupDto에서 확인
            Member admin = Member.builder()
                    .username(adminName)
                    .password(passwordEncoder.encode(adminPassword))
                    .email("admin12341234@naver.com")
                    // 해당 email은 임시
                    .role(MemberRoleEnum.ADMIN)
                    .build();
            memberRepository.save(admin);
        }
    }

    public ApiResponseDto<?> signup(SignupRequestDto requestDto){
        String username = requestDto.getUsername();

        Optional<Member> checkUsername = memberRepository.findByUsername(username);
        if(checkUsername.isPresent()){
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        String rawPassword = requestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        String email = requestDto.getEmail();
        Optional<Member> checkEmail = memberRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        Member member = Member.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .role(MemberRoleEnum.USER)
                .build();

        memberRepository.save(member);
        return new ApiResponseDto("회원 가입 성공",200,null);
    }

    @Transactional
    public ApiResponseDto<?> updateMember(long memberId, ProfileUpdateRequestDto requestDto){
        Member memberEntity = memberRepository.findById(memberId).orElseThrow(
                ()-> new IllegalArgumentException("해당 id는 존재하지 않습니다.")
        );

        if(!requestDto.getPasswordConfirm().equals(requestDto.getPassword())){
            throw new IllegalArgumentException("1차 비밀번호와 2차 비밀번호가 다릅니다.");
        }

        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 이메일 중복 검증

        String encodedPassowrd = passwordEncoder.encode(requestDto.getPassword());

        // 최근 3번안에 사용한 비밀번호는 사용할 수 없도록 제한합니다.
        // 해당 사항 만들기

        memberEntity.update(requestDto, encodedPassowrd);
        memberRepository.save(memberEntity);
        return new ApiResponseDto<>("유저 프로필 수정 성공",200,null);
    }

    @Transactional
    public ApiResponseDto<?> updateMemberProfileImage(long memberId, MultipartFile imageFile, MemberDetailsImpl memberDetails) {
        UUID uuid = UUID.randomUUID();
        // image파일 이름의 중복을 방지하기 위해 uuid사용
        String uuidImage = uuid+"_"+imageFile.getOriginalFilename();

        log.info("uuidImage : "+uuidImage);
        // db에서 저장되는 uuid형식
        Path imageFilePath = Paths.get(uploadFolder+uuidImage);

        try {
            Files.write(imageFilePath, imageFile.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }

        Member memberEntity = memberRepository.findById(memberId).orElseThrow(
                ()-> new IllegalArgumentException("해당 유저는 존재하지 않습니다.")
        );
        String testImageFilePath = imageFilePath.toString();
        log.info("testImageFilePath : "+testImageFilePath);

        memberEntity.updateProfileImageUrl(uuidImage);
        memberRepository.save(memberEntity);
        return new ApiResponseDto<>("회원 프로필 사진 변경 성공",200,null);
    }

    public ApiResponseDto<ProfileResponseDto> getMemberInfo(long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                ()-> new IllegalArgumentException("해당하는 Member Id는 존재하지 않습니다.")
        );
        ProfileResponseDto profileResponseDto = new ProfileResponseDto(member.getUsername(), member.getIntroduction());
        log.info("profileResponseDto.getUsername() : "+profileResponseDto.getUsername());
        log.info("profileResponseDto.getIntroduction() : "+profileResponseDto.getIntroduction());
        return new ApiResponseDto<>("해당 유저 정보 조회 성공",200, profileResponseDto);
    }
}