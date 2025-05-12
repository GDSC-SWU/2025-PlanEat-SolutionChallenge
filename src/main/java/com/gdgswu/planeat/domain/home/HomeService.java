package com.gdgswu.planeat.domain.home;

import com.gdgswu.planeat.domain.food.FoodService;
import com.gdgswu.planeat.domain.food.dto.FoodResponse;
import com.gdgswu.planeat.domain.history.History;
import com.gdgswu.planeat.domain.history.HistoryRepository;
import com.gdgswu.planeat.domain.history.HistoryResponse;
import com.gdgswu.planeat.domain.user.User;
import com.gdgswu.planeat.domain.user.UserRepository;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.gdgswu.planeat.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class HomeService {
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final FoodService foodService;

    public HomeResponse getHomeInfo() {
        User user = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<History> histories = historyRepository.findAllByUserIdAndCreatedAtBetween(user.getId(), startOfDay, endOfDay);
        List<HistoryResponse> todayHistories = new ArrayList<>(histories.size());
        Double totalCal = 0D;
        Double totalCarb = 0D;
        Double totalProtein = 0D;
        Double totalFat = 0D;

        for (History history : histories) {
            FoodResponse mainFood = foodService.getFoodById(history.getMainFoodId());
            todayHistories.add(HistoryResponse.builder()
                    .historyId(history.getId())
                    .mainFoodName(mainFood.getName())
                    .mainFoodImgUrl(mainFood.getImageUrl())
                    .totalCalories(history.getTotalCalories())
                    .sideFood1ImgUrl(foodService.getFoodById(history.getSideFoodId1()).getImageUrl())
                    .sideFood2ImgUrl(foodService.getFoodById(history.getSideFoodId2()).getImageUrl())
                    .createdAt(history.getCreatedAt())
                    .build());
            totalCal += history.getTotalCalories();
            totalCarb += history.getTotalCarbs();
            totalProtein += history.getTotalProtein();
            totalFat += history.getTotalFat();
        }

        return HomeResponse.builder()
                .userName(user.getName())
                .todayTotalCal(totalCal)
                .todayTotalCarb(totalCarb)
                .todayTotalProtein(totalProtein)
                .todayTotalFat(totalFat)
                .todayHistories(todayHistories)
                .build();
    }
}
