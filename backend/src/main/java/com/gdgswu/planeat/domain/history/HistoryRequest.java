package com.gdgswu.planeat.domain.history;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryRequest {
    private String foodName;
    private String foodImgUrl;

    private String recommendReason;

    private Double totalCalories;
    private Double totalCarbs;
    private Double totalProtein;
    private Double totalFat;
}