package com.tmproject.api.member.controller;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.member.dto.ProfileRequestDto;
import com.tmproject.api.member.dto.SignupRequestDto;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.service.MemberService;
import com.tmproject.global.common.ApiResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<?>> signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            for(FieldError error : bindingResult.getFieldErrors()){
                log.error(error.getField()+ ": "+ error.getDefaultMessage());
                return new ResponseEntity<>(new ApiResponseDto<>("회원 가입 요청 실패", 400, error.getDefaultMessage()),HttpStatus.BAD_REQUEST);
            }
        }
        memberService.signup(requestDto);
        return new ResponseEntity<>(new ApiResponseDto<>("회원 가입 요청 성공", 200, null),HttpStatus.OK);
        // SignupDto 제약 조건 확인
    }

    @ResponseBody
    @PutMapping("/profile/{memberId}")
    public ResponseEntity<ApiResponseDto<?>> updateProfile(
            @PathVariable long memberId,
            @RequestBody ProfileRequestDto requestDto){
            memberService.updateMember(memberId, requestDto);
        return new ResponseEntity<>(new ApiResponseDto<>("유저 프로필 수정 성공", 200, null), HttpStatus.OK);
    }

    @PutMapping("/profile/{memberId}/profileImageUrl")
    public ResponseEntity<ApiResponseDto<?>> updateProfileImage(
            @PathVariable long memberId,
            @RequestPart MultipartFile profileImage,
            @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        memberService.updateMemberProfileImage(memberId, profileImage, memberDetails);
        return new ResponseEntity<>(new ApiResponseDto<>("회원 프로필 사진 변경 성공",200,null),HttpStatus.OK);
    }
}