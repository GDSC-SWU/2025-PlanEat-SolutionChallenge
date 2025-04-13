package com.gdgswu.planeat.domain.recommend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class RecInputRequest {
    private int mealsPerDay;
    private int hungerCycleHours;
    private Boolean canCook;
    private Set<Long> preferredFoodIds;
}
