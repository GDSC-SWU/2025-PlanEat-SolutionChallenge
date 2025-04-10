package com.gdgswu.planeat.domain.user;

import com.gdgswu.planeat.domain.food.Food;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    private String gender;
    private int age;
    private int height;
    private int weight;

    private int mealsPerDay; // 하루 식사 횟수
    private int hungerCycleHours; // 배고픔 주기(시간 단위)
    private Boolean canCook; // 요리 가능 여부
    private String location;


    @CreatedDate
    private LocalDateTime joinedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "user_preferred_food",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    private Set<Food> preferredFoods = new HashSet<>(); // 선호 음식

    public void updateInfo(String name, String gender, int age, int height, int weight,
                           int mealsPerDay, int hungerCycleHours, Boolean canCook,
                           String location, Set<Food> preferredFoods) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.mealsPerDay = mealsPerDay;
        this.hungerCycleHours = hungerCycleHours;
        this.canCook = canCook;
        this.location = location;
        this.preferredFoods = preferredFoods;
    }
}
