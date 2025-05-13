package com.gdgswu.planeat.domain.history;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryRequest {
    private Long mainFoodId;
    private Long sideFoodId1;
    private Long sideFoodId2;

    private String recommendReason;

    private Double totalCalories;
    private Double totalCarbs;
    private Double totalProtein;
    private Double totalFat;
}
