package com.tmproject.api.member.controller;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.member.dto.ProfileResponseDto;
import com.tmproject.api.member.dto.ProfileUpdateRequestDto;
import com.tmproject.api.member.dto.SignupRequestDto;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.service.MemberService;
import com.tmproject.global.common.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        ApiResponseDto<?> apiResponseDto = memberService.signup(requestDto);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.valueOf(apiResponseDto.getStatusCode()));
        // SignupDto 제약 조건 확인
    }

    @ResponseBody
    @PutMapping("/profile/{memberId}")
    public ResponseEntity<ApiResponseDto<?>> updateProfile(
            @PathVariable long memberId,
            @RequestBody ProfileUpdateRequestDto requestDto,
            @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        ApiResponseDto<?> apiResponseDto = memberService.updateMember(memberId, requestDto, memberDetails);
        return new ResponseEntity<>(apiResponseDto , HttpStatus.valueOf(apiResponseDto.getStatusCode()));
    }

    @PutMapping("/profile/{memberId}/profileImageUrl")
    public ResponseEntity<ApiResponseDto<?>> updateProfileImage(
            @PathVariable long memberId,
            @RequestPart MultipartFile profileImage,
            @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        ApiResponseDto<?> apiResponseDto = memberService.updateMemberProfileImage(memberId, profileImage, memberDetails);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.valueOf(apiResponseDto.getStatusCode()));
    }

    @GetMapping("/profile/{memberId}")
    public ResponseEntity<ApiResponseDto<ProfileResponseDto>> getMemberInfo(
            @PathVariable long memberId,
            @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        ApiResponseDto<ProfileResponseDto> apiResponseDto = memberService.getMemberInfo(memberId, memberDetails);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.valueOf(apiResponseDto.getStatusCode()));
    }
    // 자기 자신의 정보를 확인할 수 있는 메서드
    // 자기 자신이 아닌 다른 사람도 확인이 가능해야함
}