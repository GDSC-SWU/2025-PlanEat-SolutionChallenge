package com.gdgswu.planeat.domain.auth.service;

import com.gdgswu.planeat.domain.auth.GoogleOAuth2Verifier;
import com.gdgswu.planeat.domain.auth.dto.request.LoginRequest;
import com.gdgswu.planeat.domain.auth.dto.request.SignupRequest;
import com.gdgswu.planeat.domain.auth.dto.response.LoginResponse;
import com.gdgswu.planeat.domain.auth.jwt.JwtProvider;
import com.gdgswu.planeat.domain.user.User;
import com.gdgswu.planeat.domain.user.UserRepository;
import com.gdgswu.planeat.domain.auth.dto.response.GoogleUserInfo;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2Service {

    private final GoogleOAuth2Verifier googleOAuth2Verifier;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {
        GoogleUserInfo userInfo = googleOAuth2Verifier.verifyIdToken(request.getIdToken());

        Optional<User> user = userRepository.findByEmail(userInfo.getEmail());
        // 기존 유저일 경우 로그인 성공, jwt 발급
        if (user.isPresent()) {
            return LoginResponse.builder()
                    .jwt(jwtProvider.createToken(user.get().getEmail()))
                    .userId(user.get().getId())
                    .build();
        } else { // 신규 유저일 경우 로그인 실패, 회원가입 필요 예외처리
            throw new CustomException(ErrorCode.SIGNUP_REQUIRED);
        }
    }

    public LoginResponse signup(SignupRequest request) {
        GoogleUserInfo userInfo = googleOAuth2Verifier.verifyIdToken(request.getIdToken());

        // 이미 가입된 이메일일 경우 예외
        if (userRepository.existsByEmail(userInfo.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(userInfo.getEmail())
                .name(request.getName())
                .age(request.getAge())
                .height(request.getHeight())
                .weight(request.getWeight())
                .location(request.getLocation())
                .build();
        userRepository.save(user);

        return LoginResponse.builder()
                .jwt(jwtProvider.createToken(user.getEmail()))
                .userId(user.getId())
                .build();
    }
}
