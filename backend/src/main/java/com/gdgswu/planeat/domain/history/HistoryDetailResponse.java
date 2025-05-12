package com.gdgswu.planeat.domain.history;

import com.gdgswu.planeat.domain.food.dto.FoodResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class HistoryDetailResponse {
    private Long historyId;

    private FoodResponse mainFood;
    private FoodResponse sideFood1;
    private FoodResponse sideFood2;

    private String recommendReason;

    private Double totalCalories;
    private Double totalCarbs;
    private Double totalProtein;
    private Double totalFat;

    private LocalDateTime createdAt;
}
