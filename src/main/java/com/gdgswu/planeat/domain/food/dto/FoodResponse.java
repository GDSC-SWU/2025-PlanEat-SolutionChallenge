package com.gdgswu.planeat.domain.food.dto;

import com.gdgswu.planeat.domain.food.Food;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FoodResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private String category;
    private Double cost;
    private String costUnit;

    private Integer calories;
    private Double carbs;
    private Double protein;
    private Double fat;
    private String weightPerServing;

    public static FoodResponse from(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .imageUrl(food.getImageUrl())
                .category(food.getCategory())
                .cost(food.getCost())
                .costUnit(food.getCostUnit())
                .calories(food.getCalories())
                .carbs(food.getCarbs())
                .protein(food.getProtein())
                .fat(food.getFat())
                .weightPerServing(food.getWeightPerServing())
                .build();
    }

    public Food toEntity() {
        return Food.builder()
                .id(id)
                .name(name)
                .imageUrl(imageUrl)
                .category(category)
                .cost(cost)
                .costUnit(costUnit)
                .calories(calories)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .weightPerServing(weightPerServing)
                .build();
    }
}
