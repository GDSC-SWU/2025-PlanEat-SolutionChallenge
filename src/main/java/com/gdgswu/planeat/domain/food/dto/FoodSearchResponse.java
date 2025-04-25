package com.gdgswu.planeat.domain.food.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class FoodSearchResponse {
    private Long id;
    private String name;
    private List<String> possibleUnits;
}
