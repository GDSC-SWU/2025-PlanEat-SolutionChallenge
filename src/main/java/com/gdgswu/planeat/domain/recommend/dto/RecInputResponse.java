package com.gdgswu.planeat.domain.recommend.dto;

import com.gdgswu.planeat.domain.food.Food;
import com.gdgswu.planeat.domain.recommend.RecInput;
import com.gdgswu.planeat.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class RecInputResponse {
    // base user info
    private String gender;
    private int age;
    private int height;
    private int weight;
    private String location;

    // additional info for recommend
    private int mealsPerDay;
    private int hungerCycleHours;
    private Boolean canCook;
    private Set<Food> preferredFoods;

    public static RecInputResponse of(User user, RecInput request) {
        return RecInputResponse.builder()
                .gender(user.getGender())
                .age(user.getAge())
                .height(user.getHeight())
                .weight(user.getWeight())
                .location(user.getLocation())
                .mealsPerDay(request.getMealsPerDay())
                .hungerCycleHours(request.getHungerCycleHours())
                .canCook(request.getCanCook())
                .preferredFoods(request.getPreferredFoods())
                .build();
    }
}
