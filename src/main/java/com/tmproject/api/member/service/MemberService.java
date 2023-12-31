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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j(topic = "Member Service")
@Service
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

    private static Stack<String> passwordHistory = new Stack<>();

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
            return new ApiResponseDto<>("중복된 사용자가 존재합니다.", 400, null);
        }
        String rawPassword = requestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        String email = requestDto.getEmail();
        Optional<Member> checkEmail = memberRepository.findByEmail(email);

        if (checkEmail.isPresent()) {
            return new ApiResponseDto<>("중복된 Email이 존재합니다.", 400, null);
        }
        // 회원 가입을 할 때는 중복 Email불가

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
    public ApiResponseDto<?> updateMember(long memberId, ProfileUpdateRequestDto requestDto, MemberDetailsImpl memberDetails){
        Member memberEntity = memberRepository.findById(memberId).orElse(null);
        if(memberEntity == null){
            return new ApiResponseDto<>("해당 id는 존재하지 않습니다.", 200,null);
        }

        log.info("requestDto.getUsername() : "+requestDto.getUsername());
        log.info("memberDetails.getUsername() : "+memberDetails.getMember().getUsername());
        if(!memberDetails.getMember().getUsername().equals(requestDto.getUsername())){
            return new ApiResponseDto<>("유저 네임은 변경할 수 없습니다.", 400, null);
        }

        if (memberDetails.getMember().getId() != memberId) {
            // id 1번은 무조건 관리자 아이디이기에
            return new ApiResponseDto<>("해당 사용자는 권한이 없습니다.", 403, null);
        }

        if(!requestDto.getPasswordConfirm().equals(requestDto.getPassword())){
            return new ApiResponseDto<>("1차 비밀번호와 2차 비밀번호가 다릅니다.", 400, null);
        }

        String requestEmail = requestDto.getEmail();
        log.info("requestEmail : "+ requestEmail);
        log.info("memberEntity.getEmail() : "+memberEntity.getEmail());
        System.out.println("requestEmail.equals(memberEntity.getEmail()) : "+requestEmail.equals(memberEntity.getEmail()));
        System.out.println("memberRepository.existsByEmail(requestEmail) : "+memberRepository.existsByEmail(requestEmail));

        if (memberRepository.existsByEmailAndIdNot(requestEmail, memberId)) {
            return new ApiResponseDto<>("이미 사용 중인 이메일입니다.", 400, null);
        }
        // 이메일 중복 검증 확인 amdin email과 member email 검증

        if (!isPasswordAllowed(requestDto.getPassword())) {
            return new ApiResponseDto<>("해당 비밀번호는 최근 3번 안에 사용한 비밀번호이기에 사용할 수 없습니다.", 400, null);
        }
        // 새 비밀번호가 마지막 세 개의 비밀번호 중 하나와 일치하는지 확인

        passwordHistory.push(requestDto.getPassword());
        log.info("passwordHistory.size() : "+passwordHistory.size());
        // 새 비밀번호를 스택에 추가

        String encodedPassowrd = passwordEncoder.encode(requestDto.getPassword());

        memberEntity.update(requestDto, encodedPassowrd);
        memberRepository.save(memberEntity);
        return new ApiResponseDto<>("유저 프로필 수정 성공",200,null);
    }

    private boolean isPasswordAllowed(String requestPassword) {
        if (passwordHistory.size() >= 3) {
            // passwordHistory가 3개 이상인 경우
            // 모든 passwordHistory에서 firstElement와 match되는지 확인
            boolean allEqual = passwordHistory.stream().allMatch(element -> element.equals(requestPassword));
            // 푸시는 해야함
            passwordHistory.push(requestPassword);

            if(!allEqual){
                // 단 한개도 매칭되는게 없음
                return true;
            }else{
                // 한개 이상 매칭되는게 존재
                int matchPassword = 0;
                for(int i = 0; i<passwordHistory.size(); i++){
                    int match = passwordHistory.search(requestPassword);       // 0 or 1
                    if(match == 1){
                        matchPassword++;
                    }
                }
                if(matchPassword <= 3){
                    return true;
                }else{
                    passwordHistory.pop();
                    return false;
                }
            }
        }
        // passwordHistory가 3개 미만인 경우는 모두 허용
        return true;
    }

    @Transactional
    public ApiResponseDto<?> updateMemberProfileImage(long memberId, MultipartFile imageFile, MemberDetailsImpl memberDetails) {
        Member memberEntity = memberRepository.findById(memberId).orElse(null);
        if(memberEntity == null){
            return new ApiResponseDto<>("해당 Member Id는 존재하지 않습니다", 400, null);
        }
        log.info("memberId : "+memberId);
        log.info("memberDetails.getMember().getId() : "+memberDetails.getMember().getId());
        if (memberDetails.getMember().getId() != memberId) {
            // id 1번은 무조건 관리자 아이디이기에
            return new ApiResponseDto<>("해당 사용자는 권한이 없습니다.", 403, null);
        }

        String originalFilename = imageFile.getOriginalFilename();
        log.info("originalFilename : "+originalFilename);

        StringTokenizer st = new StringTokenizer(originalFilename, ".");
        log.info("filename : "+st.nextToken());

        String fileExtension = st.nextToken();
        log.info("fileExtension : "+fileExtension);

        // 허용되는 이미지 확장자 목록 (예: jpg, jpeg, png 등)
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");

        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
            return new ApiResponseDto<>("지원하지 않는 이미지 확장자입니다.", 400, null);
        }

        UUID uuid = UUID.randomUUID();
        // image파일 이름의 중복을 방지하기 위해 uuid사용
        String uuidImage = uuid+"_"+originalFilename;

        log.info("uuidImage : "+uuidImage);
        // db에서 저장되는 uuid형식
        Path imageFilePath = Paths.get(uploadFolder+uuidImage);

        try {
            Files.write(imageFilePath, imageFile.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }

        String testImageFilePath = imageFilePath.toString();
        log.info("testImageFilePath : "+testImageFilePath);

        memberEntity.updateProfileImageUrl(uuidImage);
        memberRepository.save(memberEntity);
            return new ApiResponseDto<>("회원 프로필 사진 변경 성공",200,null);
        }
        public ApiResponseDto<ProfileResponseDto> getMemberInfo(long memberId) {
            log.info("memberId : " + memberId);

            if(memberId == 1L){
                return new ApiResponseDto<>("접근 불가", 403, null);
        }

        Member member = memberRepository.findById(memberId).orElse(null);
        if(member == null){
            return new ApiResponseDto<>("해당하는 Member Id는 존재하지 않습니다.", 400, null);
        }

        ProfileResponseDto profileResponseDto = ProfileResponseDto
                .builder()
                .username(member.getUsername())
                .introduction(member.getIntroduction())
                .profileImageUrl(member.getProfileImageUrl())
                .build();

        log.info("profileResponseDto.getUsername() : " + profileResponseDto.getUsername());
        log.info("profileResponseDto.getIntroduction() : " + profileResponseDto.getIntroduction());

        return new ApiResponseDto<>("해당 유저 정보 조회 성공", 200, profileResponseDto);
    }

    @Transactional(readOnly = true)
    public ApiResponseDto<?> getMemberListInfo() {
        List<Member> memberList = memberRepository.findAll();
        List<ProfileResponseDto> responseList = new ArrayList<>();
        if(memberList.size() == 1){
            return new ApiResponseDto<>("관리자 제외 멤버가 존재하지 않습니다.",400, null);
        }
        for(int i =1; i<memberList.size(); i++){
            responseList.add(new ProfileResponseDto(memberList.get(i)));
            // admin 계정이 무조건 id가 1이기에 절대 안됨, memberList.get(0) 접근 불가
        }
        return new ApiResponseDto<>("모든 멤버 조회 성공",200,responseList);
    }
}