package com.gdgswu.planeat.domain.auth.dto.request;

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
    private String location;

    private int mealsPerDay;
    private int hungerCycle;
    private Boolean canCook;
    private Set<Long> allergicFoodIds;
}
