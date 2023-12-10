package com.tmproject.api.follow.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.board.controller.BoardController;
import com.tmproject.api.follow.service.FollowService;
import com.tmproject.api.member.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FollowService followService;

    @InjectMocks
    private FollowController followController;


    @BeforeEach
    public void setUp() {
        // 인증 정보 설정
        Authentication authentication = Mockito.mock(Authentication.class);
        MemberDetailsImpl memberDetails = Mockito.mock(MemberDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(memberDetails);
        when(memberDetails.getUsername()).thenReturn("testUser"); // 사용자명 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("팔로우 유저 테스트")
    public void followUserTest() throws Exception {
        // Given
        Long followerId = 1L;

        // When
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/member/{followerId}/follow", followerId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        // Then
        verify(followService).followUser(anyString(), anyLong());
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assertions.assertEquals("요청 성공", mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("언팔로우 유저 테스트")
    public void unFollowUserTest() throws Exception {
        // Given
        Long followerId = 1L;

        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/member/{followerId}/follow", followerId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        // Then
        verify(followService).unFollowUser(anyString(), anyLong());
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        Assertions.assertEquals("요청 성공", mvcResult.getResponse().getContentAsString());
    }
}