package com.gdgswu.planeat.domain.food.dto;

import com.gdgswu.planeat.domain.food.Food;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodResponse {
    private String id;
    private String name;
    private String category;
    private Integer calories;
    private Double carbs;
    private Double protein;
    private Double fat;
    private String portionWeight;

    public static FoodResponse from(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .category(food.getCategory())
                .calories(food.getCalories())
                .carbs(food.getCarbs())
                .protein(food.getProtein())
                .fat(food.getFat())
                .portionWeight(food.getPortionWeight())
                .build();
    }
}
