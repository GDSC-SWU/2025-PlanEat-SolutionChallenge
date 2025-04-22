package com.gdgswu.planeat.domain.user;

import com.gdgswu.planeat.domain.food.Food;
import com.gdgswu.planeat.domain.food.FoodRepository;
import com.gdgswu.planeat.domain.user.dto.UserRequest;
import com.gdgswu.planeat.domain.user.dto.UserResponse;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import com.gdgswu.planeat.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.gdgswu.planeat.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    public UserResponse get() {
        User user = getLoginUser();
        return UserResponse.from(user, user.getAllergicFoods());
    }

    public UserResponse edit(UserRequest request) {
        User user = getLoginUser();

        Set<Food> newFoodSet = new HashSet<>();
        for (Long foodId : request.getAllergicFoodIds()) {
            newFoodSet.add(foodRepository.findById(foodId).orElseThrow(
                    () -> new CustomException(ErrorCode.INVALID_FOOD_ID)
            ));
        }
        user.updateInfo(request, newFoodSet);

        return UserResponse.from(user, user.getAllergicFoods());
    }

    public void delete() {
        userRepository.delete(getLoginUser());
    }

    private User getLoginUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
