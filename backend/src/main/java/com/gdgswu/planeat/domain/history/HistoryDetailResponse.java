package com.gdgswu.planeat.domain.history;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class HistoryDetailResponse {
    private Long historyId;

    private String foodName;
    private String foodImgUrl;

    private String recommendReason;

    private Double totalCalories;
    private Double totalCarbs;
    private Double totalProtein;
    private Double totalFat;

    private LocalDateTime createdAt;
}