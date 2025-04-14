package com.gdgswu.planeat.domain.food;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Food {
    @Id
    @Column(name = "food_id")
    private String id; // FOOD_CD

    private String name; //FOOD_NM_KR
    private String category; // FOOD_CAT1_NM

    private Integer calories; // AMT_NUM1
    private Double carbs; // AMT_NUM6
    private Double protein; // AMT_NUM3
    private Double fat; // AMT_NUM4
    private String portionWeight; // SERVING_SIZE
}
