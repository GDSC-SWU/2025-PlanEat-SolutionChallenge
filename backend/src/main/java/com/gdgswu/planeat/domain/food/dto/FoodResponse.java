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

    public static FoodResponse from(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .imageUrl(food.getImageUrl())
                .build();
    }

    public Food toEntity() {
        return Food.builder()
                .id(id)
                .name(name)
                .imageUrl(imageUrl)
                .build();
    }
}
