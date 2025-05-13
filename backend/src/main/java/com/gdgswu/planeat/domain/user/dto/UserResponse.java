package com.gdgswu.planeat.domain.user.dto;

import com.gdgswu.planeat.domain.food.Food;
import com.gdgswu.planeat.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private String location;

    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;

    private int mealsPerDay;
    private int hungerCycle;
    private Boolean canCook;
    private Set<String> intolerances;

    public static UserResponse from(User user, Set<String> intolerances) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .gender(user.getGender())
                .age(user.getAge())
                .height(user.getHeight())
                .weight(user.getWeight())
                .location(user.getLocation())
                .joinedAt(user.getJoinedAt())
                .updatedAt(user.getUpdatedAt())
                .mealsPerDay(user.getMealsPerDay())
                .hungerCycle(user.getHungerCycle())
                .canCook(user.getCanCook())
                .intolerances(intolerances)
                .build();
    }
}
