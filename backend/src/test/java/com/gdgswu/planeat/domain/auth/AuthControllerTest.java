package com.gdgswu.planeat.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgswu.planeat.domain.auth.dto.request.LoginRequest;
import com.gdgswu.planeat.domain.auth.dto.request.SignupRequest;
import com.gdgswu.planeat.domain.auth.dto.response.LoginResponse;
import com.gdgswu.planeat.domain.auth.jwt.JwtProvider;
import com.gdgswu.planeat.domain.auth.service.AuthService;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // 필터 제거
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("로그인 요청 처리 성공시 jwt와 userId 반환")
    void login_success() throws Exception {
        // given
        LoginResponse response = new LoginResponse("test_jwt_token", 1L);
        given(authService.login(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("valid_token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.jwt").value(response.getJwt()))
                .andExpect(jsonPath("$.data.userId").value(response.getUserId()));
    }

    @Test
    @DisplayName("잘못된 idToken일 경우 400 Bad Request")
    void login_fail() throws Exception {
        // given
        given(authService.login(any(LoginRequest.class)))
                .willThrow(new CustomException(ErrorCode.ID_TOKEN_INVALID));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("fake_token"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.ID_TOKEN_INVALID.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.ID_TOKEN_INVALID.name()));
    }

    @Test
    @DisplayName("회원가입 요청 처리 성공시 jwt와 userId 반환")
    void signup_success() throws Exception {
        // given
        LoginResponse  response = new LoginResponse("test_jwt_token", 1L);
        given(authService.signup(any(SignupRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                            .contentType(APPLICATION_JSON)
                        .   content(objectMapper.writeValueAsString(SignupRequest.builder().build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.jwt").value(response.getJwt()))
                .andExpect(jsonPath("$.data.userId").value(response.getUserId()));
    }

    @Test
    @DisplayName("유효하지 않은 idToken으로 회원가입 요청시 400 Bad Request")
    void signup_fail() throws Exception {
        // given
        given(authService.signup(any(SignupRequest.class)))
                .willThrow(new CustomException(ErrorCode.ID_TOKEN_INVALID));

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SignupRequest.builder().build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.ID_TOKEN_INVALID.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.ID_TOKEN_INVALID.name()));
    }
}