package com.gdgswu.planeat.domain.food;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long id;

    private String name;
    private String category;

    @Builder.Default
    private Integer calories = 0; // 열량 (kcal)

    @Builder.Default
    private Double carbs = 0.0; // 탄수화물 (g)

    @Builder.Default
    private Double protein = 0.0; // 단백질 (g)

    @Builder.Default
    private Double fat = 0.0; // 지방 (g)

    private Double portionWeight; // 식품 중량 (g)
}
