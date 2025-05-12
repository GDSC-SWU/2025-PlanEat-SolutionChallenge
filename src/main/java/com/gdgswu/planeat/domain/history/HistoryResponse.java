package com.gdgswu.planeat.domain.history;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistoryResponse {
    private Long historyId;
    private String mainFoodName;
    private String mainFoodImgUrl;
    private double totalCalories;
    private String sideFood1ImgUrl;
    private String sideFood2ImgUrl;
    private LocalDateTime createdAt;
}
