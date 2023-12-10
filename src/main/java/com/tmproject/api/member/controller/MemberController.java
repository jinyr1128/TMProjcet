package com.tmproject.api.member.controller;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.member.dto.ProfileResponseDto;
import com.tmproject.api.member.dto.ProfileUpdateRequestDto;
import com.tmproject.api.member.dto.SignupRequestDto;
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

@Slf4j(topic = "Member Controller")
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

    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDto<?>> getMemberListInfo(){
        ApiResponseDto<?> apiResponseDto = memberService.getMemberListInfo();
        return new ResponseEntity<>(apiResponseDto, HttpStatus.valueOf(apiResponseDto.getStatusCode()));
    }
}