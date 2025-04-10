package com.gdgswu.planeat.domain.user;

import com.gdgswu.planeat.domain.food.Food;
import com.gdgswu.planeat.domain.food.FoodRepository;
import com.gdgswu.planeat.domain.user.dto.request.UserRequest;
import com.gdgswu.planeat.domain.user.dto.response.UserResponse;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import com.gdgswu.planeat.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gdgswu.planeat.global.exception.ErrorCode.INVALID_FOOD_ID;
import static com.gdgswu.planeat.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    public UserResponse get() {
        return UserResponse.from(getLoginUser());
    }

    public UserResponse edit(UserRequest request) {
        User user = getLoginUser();
        Set<Food> preferredFoods = resolvePreferredFoods(request);

        user.updateInfo(
                request.getName(),
                request.getGender(),
                request.getAge(),
                request.getHeight(),
                request.getWeight(),
                request.getMealsPerDay(),
                request.getHungerCycleHours(),
                request.getCanCook(),
                request.getLocation(),
                preferredFoods
        );
        return UserResponse.from(user);
    }

    private Set<Food> resolvePreferredFoods(UserRequest request) {
        Set<Long> ids = request.getPreferredFoodIds();
        if (ids == null || ids.isEmpty()) return new HashSet<>();

        Set<Food> foods = new HashSet<>(foodRepository.findAllById(ids));
        if (foods.size() != ids.size()) {
            throw new CustomException(INVALID_FOOD_ID);
        }
        return foods;
    }

    public void delete() {
        userRepository.delete(getLoginUser());
    }

    private User getLoginUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
