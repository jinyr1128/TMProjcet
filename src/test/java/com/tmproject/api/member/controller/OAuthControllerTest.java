package com.tmproject.api.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.Common.Security.WebSecurityConfig;
import com.tmproject.api.member.dto.SignupRequestDto;
import com.tmproject.api.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

public class OAuthControllerTest {
    @WebMvcTest(
            controllers = {MemberController.class},
            excludeFilters = {
                    @ComponentScan.Filter(
                            type = FilterType.ASSIGNABLE_TYPE,
                            classes = WebSecurityConfig.class
                    )
            }
    )
    public static class MemberControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MemberService memberService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("[MemberController] signup success")
        void signupSuccess(){
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto("testUsername", "password","test@email.com");
        }
    }
}
