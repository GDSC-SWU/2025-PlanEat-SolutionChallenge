package com.gdgswu.planeat.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UserRequest {
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
