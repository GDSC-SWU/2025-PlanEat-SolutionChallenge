package com.gdgswu.planeat.domain.food.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FoodSearchResponse {
    private Long id;
    private String name;
}
