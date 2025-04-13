package com.gdgswu.planeat.domain.recommend;

import com.gdgswu.planeat.domain.food.Food;
import com.gdgswu.planeat.domain.food.FoodRepository;
import com.gdgswu.planeat.domain.recommend.dto.RecInputRequest;
import com.gdgswu.planeat.domain.recommend.dto.RecInputResponse;
import com.gdgswu.planeat.domain.user.User;
import com.gdgswu.planeat.domain.user.UserRepository;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import com.gdgswu.planeat.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.gdgswu.planeat.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class RecInputService {

    private final RecInputRepository recInputRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public RecInputResponse save(RecInputRequest request) {

        Set<Food> preferredFoods = new HashSet<>();
        if (request.getPreferredFoodIds() != null && !request.getPreferredFoodIds().isEmpty()) {
            preferredFoods = new HashSet<>(foodRepository.findAllById(request.getPreferredFoodIds()));
            if (preferredFoods.size() != request.getPreferredFoodIds().size()) {
                throw new CustomException(ErrorCode.INVALID_FOOD_ID);
            }
        }
        User user = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        RecInput savedRequest = recInputRepository.save(RecInput.builder()
                .user(user)
                .mealsPerDay(request.getMealsPerDay())
                .hungerCycleHours(request.getHungerCycleHours())
                .canCook(request.getCanCook())
                .preferredFoods(preferredFoods)
                .build());

        return RecInputResponse.of(user, savedRequest);

    }
}
