package com.gdgswu.planeat.domain.user.dto.response;

import com.gdgswu.planeat.domain.food.Food;
import com.gdgswu.planeat.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
public class UserResponse {
    private Long id;

    private String email;

    private String name;
    private String gender;
    private int age;
    private int height;
    private int weight;

    private int mealsPerDay;
    private int hungerCycleHours;
    private Boolean canCook;
    private String location;


    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;

    private Set<Food> preferredFoods;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .gender(user.getGender())
                .age(user.getAge())
                .height(user.getHeight())
                .weight(user.getWeight())
                .mealsPerDay(user.getMealsPerDay())
                .hungerCycleHours(user.getHungerCycleHours())
                .canCook(user.getCanCook())
                .location(user.getLocation())
                .joinedAt(user.getJoinedAt())
                .updatedAt(user.getUpdatedAt())
                .preferredFoods(user.getPreferredFoods())
                .build();
    }
}
