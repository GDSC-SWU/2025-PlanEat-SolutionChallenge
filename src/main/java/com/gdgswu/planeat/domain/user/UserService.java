package com.gdgswu.planeat.domain.user;

import com.gdgswu.planeat.domain.user.dto.UserRequest;
import com.gdgswu.planeat.domain.user.dto.UserResponse;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gdgswu.planeat.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponse get() {
        return UserResponse.from(getLoginUser());
    }

    public UserResponse edit(UserRequest request) {
        User user = getLoginUser();

        user.updateInfo(
                request.getName(),
                request.getGender(),
                request.getAge(),
                request.getHeight(),
                request.getWeight(),
                request.getLocation()
        );
        return UserResponse.from(user);
    }

    public void delete() {
        userRepository.delete(getLoginUser());
    }

    private User getLoginUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
