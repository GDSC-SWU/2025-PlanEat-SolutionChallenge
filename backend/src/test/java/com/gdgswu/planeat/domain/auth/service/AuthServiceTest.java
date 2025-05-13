package com.gdgswu.planeat.domain.auth.service;

import com.gdgswu.planeat.domain.auth.firebase.FirebaseTokenVerifierImpl;
import com.gdgswu.planeat.domain.auth.dto.request.LoginRequest;
import com.gdgswu.planeat.domain.auth.dto.request.SignupRequest;
import com.gdgswu.planeat.domain.auth.dto.response.LoginResponse;
import com.gdgswu.planeat.domain.auth.jwt.JwtProvider;
import com.gdgswu.planeat.domain.user.User;
import com.gdgswu.planeat.domain.user.UserRepository;
import com.gdgswu.planeat.global.exception.CustomException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.gdgswu.planeat.global.exception.ErrorCode.SIGNUP_REQUIRED;
import static com.gdgswu.planeat.global.exception.ErrorCode.USER_ALREADY_EXISTS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private FirebaseTokenVerifierImpl firebaseTokenVerifierImpl;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FirebaseToken firebaseToken;

    @InjectMocks
    private AuthService authService;

    private final String email = "test@gmail.com";
    private final String idToken = "invalid Token";
    private final String jwt = "jwt.token";

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .email(email)
                .name("user")
                .age(25)
                .height(160)
                .weight(53)
                .location("Seoul")
                .build();
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        tokenAndJwtGivenProcess();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        LoginRequest request = new LoginRequest(idToken);

        // when
        LoginResponse result = authService.login(request);

        // then
        assertThat(result.getJwt()).isEqualTo(jwt);
        assertThat(result.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("미가입 유저 로그인 시 SIGNUP_REQUIRED 예외 발생")
    void login_fail_userNotFound() {
        // given
        given(firebaseTokenVerifierImpl.verifyIdTokenAndGetEmail(idToken)).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());
        LoginRequest request = new LoginRequest(idToken);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> authService.login(request));
        assertThat(ex.getErrorCode()).isEqualTo(SIGNUP_REQUIRED);
    }

    @Test
    @DisplayName("회원가입 성공 시 jwt 토큰과 userId 반환")
    void signup_success() {
        // given
        SignupRequest request = SignupRequest.builder()
                .idToken(idToken)
                .name(user.getName())
                .age(user.getAge())
                .height(user.getHeight())
                .weight(user.getWeight())
                .location(user.getLocation())
                .build();
        tokenAndJwtGivenProcess();
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        LoginResponse result = authService.signup(request);

        // then
        assertThat(result.getJwt()).isEqualTo(jwt);
        assertThat(result.getUserId()).isEqualTo(user.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 가입된 이메일로 회원가입 시 USER_ALREADY_EXISTS 예외 발생")
    void signup_fail_alreadyExists() {
        // given
        SignupRequest request = SignupRequest.builder()
                .idToken(idToken)
                .name(user.getName())
                .age(user.getAge())
                .height(user.getHeight())
                .weight(user.getWeight())
                .location(user.getLocation())
                .build();
        given(firebaseTokenVerifierImpl.verifyIdTokenAndGetEmail(idToken)).willReturn(email);
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> authService.signup(request));
        assertThat(ex.getErrorCode()).isEqualTo(USER_ALREADY_EXISTS);
    }

    private void tokenAndJwtGivenProcess() {
        given(firebaseTokenVerifierImpl.verifyIdTokenAndGetEmail(idToken)).willReturn(email);
        given(jwtProvider.createToken(email)).willReturn(jwt);
    }
}