package com.gdgswu.planeat.domain.auth.service;

import com.gdgswu.planeat.domain.auth.firebase.FirebaseTokenVerifier;
import com.gdgswu.planeat.domain.auth.dto.request.LoginRequest;
import com.gdgswu.planeat.domain.auth.dto.request.SignupRequest;
import com.gdgswu.planeat.domain.auth.dto.response.LoginResponse;
import com.gdgswu.planeat.domain.auth.jwt.JwtProvider;
import com.gdgswu.planeat.domain.food.Food;
import com.gdgswu.planeat.domain.food.FoodRepository;
import com.gdgswu.planeat.domain.user.User;
import com.gdgswu.planeat.domain.user.UserRepository;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.gdgswu.planeat.global.exception.ErrorCode.SIGNUP_REQUIRED;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final FirebaseTokenVerifier firebaseTokenVerifier;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {
        String email = firebaseTokenVerifier.verifyIdTokenAndGetEmail(request.getIdToken());

        // 신규 유저일 경우 로그인 실패, 회원가입 필요 예외처리
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(SIGNUP_REQUIRED));
        // 기존 유저일 경우 로그인 성공, jwt 발급
        return LoginResponse.builder()
                .jwt(jwtProvider.createToken(user.getEmail()))
                .userId(user.getId())
                .build();
    }

    public LoginResponse signup(SignupRequest request) {
        String email = firebaseTokenVerifier.verifyIdTokenAndGetEmail(request.getIdToken());

        // 이미 가입된 이메일일 경우 예외
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Set<Food> preferredFoods = Set.of();
        if (request.getPreferredFoodIds() != null && !request.getPreferredFoodIds().isEmpty()) {
            preferredFoods = new HashSet<>(foodRepository.findAllById(request.getPreferredFoodIds()));
            if (preferredFoods.size() != request.getPreferredFoodIds().size()) {
                throw new CustomException(ErrorCode.INVALID_FOOD_ID);
            }
        }

        User user = userRepository.save(User.builder()
                .email(email)
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .height(request.getHeight())
                .weight(request.getWeight())
                .mealsPerDay(request.getMealsPerDay())
                .hungerCycleHours(request.getHungerCycleHours())
                .canCook(request.getCanCook())
                .location(request.getLocation())
                .preferredFoods(preferredFoods)
                .build());

        return LoginResponse.builder()
                .jwt(jwtProvider.createToken(user.getEmail()))
                .userId(user.getId())
                .build();
    }
}
