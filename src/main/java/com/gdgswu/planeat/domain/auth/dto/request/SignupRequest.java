package com.gdgswu.planeat.domain.auth.dto.request;

import com.gdgswu.planeat.domain.food.Food;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class SignupRequest {
    private String idToken;

    private String name;
    private String gender;
    private int age;
    private int height;
    private int weight;

    private int mealsPerDay;
    private int hungerCycleHours;
    private Boolean canCook;
    private String location;

    private Set<Long> preferredFoodIds;
}
