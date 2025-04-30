package com.gdgswu.planeat.domain.auth.service;

import com.gdgswu.planeat.domain.auth.dto.request.LoginRequest;
import com.gdgswu.planeat.domain.auth.dto.request.SignupRequest;
import com.gdgswu.planeat.domain.auth.dto.response.LoginResponse;
import com.gdgswu.planeat.domain.auth.firebase.FirebaseTokenVerifier;
import com.gdgswu.planeat.domain.auth.jwt.JwtProvider;
import com.gdgswu.planeat.domain.user.User;
import com.gdgswu.planeat.domain.user.UserRepository;
import com.gdgswu.planeat.domain.user.intolerance.Intolerance;
import com.gdgswu.planeat.domain.user.intolerance.IntoleranceEnum;
import com.gdgswu.planeat.domain.user.intolerance.IntoleranceRepository;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gdgswu.planeat.global.exception.ErrorCode.SIGNUP_REQUIRED;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final FirebaseTokenVerifier firebaseTokenVerifier;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final IntoleranceRepository intoleranceRepository;

    public LoginResponse login(LoginRequest request) {
        String email = firebaseTokenVerifier.verifyIdTokenAndGetEmail(request.getIdToken());

        // If the user is new, throw an exception indicating that signup is required
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(SIGNUP_REQUIRED));
        // If the user already exists, login succeeds and JWT is issued
        return LoginResponse.builder()
                .jwt(jwtProvider.createToken(user.getEmail()))
                .userId(user.getId())
                .build();
    }

    public LoginResponse signup(SignupRequest request) {
        String email = firebaseTokenVerifier.verifyIdTokenAndGetEmail(request.getIdToken());

        // if the email is already registered
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Set<IntoleranceEnum> intoleranceEnums = request.getIntolerances().stream()
                .map(IntoleranceEnum::fromName) // "Peanut" → PEANUT
                .collect(Collectors.toSet());
        Set<Intolerance> intoleranceSet = new HashSet<>(intoleranceRepository.findAllByNameIn(intoleranceEnums));

        // Validation: throw if the number of requested names doesn't match the number of found entities
        if (intoleranceEnums.size() != intoleranceSet.size()) {
            throw new CustomException(ErrorCode.INVALID_INTOLERANCE_NAME);
        }

        User user = userRepository.save(requestToUser(request, email, intoleranceSet));

        return LoginResponse.builder()
                .jwt(jwtProvider.createToken(user.getEmail()))
                .userId(user.getId())
                .build();
    }

    private static User requestToUser(SignupRequest request, String email, Set<Intolerance> intoleranceSet) {
        return User.builder()
                .email(email)
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .height(request.getHeight())
                .weight(request.getWeight())
                .location(request.getLocation())
                .mealsPerDay(request.getMealsPerDay())
                .hungerCycle(request.getHungerCycle())
                .canCook(request.getCanCook())
                .intolerances(intoleranceSet)
                .build();
    }
}
