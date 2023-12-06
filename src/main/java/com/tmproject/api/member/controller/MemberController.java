package com.tmproject.api.member.controller;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.member.dto.ProfileRequestDto;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    @PostMapping("/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            for(FieldError error : bindingResult.getFieldErrors()){
                log.error(error.getField()+ ": "+ error.getDefaultMessage());
            }
            return "redirect:/api/member/signup";
        }
        memberService.signup(requestDto);
        return "redirect:/api/member/login";
    }

    @ResponseBody
    @PutMapping("/profile/{memberId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable long memberId,
            @RequestBody ProfileRequestDto requestDto,
            @AuthenticationPrincipal MemberDetailsImpl memberDetails
            //@RequestParam("profileImage") MultipartFile profileImageFile
            ){
        log.info("일로 안들어 오는데?");
            memberService.updateMember(memberId, requestDto, memberDetails);

        return new ResponseEntity<>(new ApiResponseDto<>("프로필 사진 업데이트 성공", 200, null), HttpStatus.OK);
    }

    @PutMapping("/profile/{memberId}/profileImageUrl")
    public ResponseEntity<?> updateProfileImage(
            @PathVariable long memberId,
            @RequestPart MultipartFile profileImage,
            @AuthenticationPrincipal MemberDetailsImpl memberDetails){
        memberService.updateMemberProfileImage(memberId, profileImage, memberDetails);
        return new ResponseEntity<>(new ApiResponseDto<>("회원 프로필 사진 변경 성공",200,null),HttpStatus.OK);
    }

}