package com.gdgswu.planeat.domain.user;

import com.gdgswu.planeat.domain.user.dto.UserRequest;
import com.gdgswu.planeat.domain.user.dto.UserResponse;
import com.gdgswu.planeat.domain.user.intolerance.Intolerance;
import com.gdgswu.planeat.domain.user.intolerance.IntoleranceEnum;
import com.gdgswu.planeat.domain.user.intolerance.IntoleranceRepository;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import com.gdgswu.planeat.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gdgswu.planeat.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final IntoleranceRepository intoleranceRepository;

    public UserResponse get() {
        User user = getLoginUser();
        return UserResponse.from(user, getUserIntolerances(user));
    }

    public UserResponse edit(UserRequest request) {
        User user = getLoginUser();

        Set<IntoleranceEnum> intoleranceEnums = request.getIntolerances().stream()
                .map(IntoleranceEnum::fromName) // "Peanut" → PEANUT
                .collect(Collectors.toSet());
        Set<Intolerance> intoleranceSet = new HashSet<>(intoleranceRepository.findAllByNameIn(intoleranceEnums));

        // Validation: throw if the number of requested names doesn't match the number of found entities
        if (intoleranceEnums.size() != intoleranceSet.size()) {
            throw new CustomException(ErrorCode.INVALID_INTOLERANCE_NAME);
        }

        user.updateInfo(request, intoleranceSet);

        return UserResponse.from(user, getUserIntolerances(user));
    }

    public void delete() {
        userRepository.delete(getLoginUser());
    }

    private User getLoginUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private static Set<String> getUserIntolerances(User user) {
        return user.getIntolerances().stream()
                .map(Intolerance::getName)
                .map(IntoleranceEnum::getName)
                .collect(Collectors.toSet());
    }
}
