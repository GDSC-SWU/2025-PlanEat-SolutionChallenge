package com.gdgswu.planeat.domain.recommend;

import com.gdgswu.planeat.domain.food.Food;
import com.gdgswu.planeat.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class RecInput {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "recommend_id")
    private Long id;

    // User table mapping (n:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // additional info for recommend
    private int mealsPerDay;
    private int hungerCycleHours;
    private Boolean canCook;

    @ManyToMany
    @JoinTable(
            name = "recommend_preferred_food",
            joinColumns = @JoinColumn(name = "recommend_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    @Builder.Default
    private Set<Food> preferredFoods = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;
}
