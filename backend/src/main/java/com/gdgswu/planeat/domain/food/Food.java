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
    private Long id;

    private String name;
    private String imageUrl;
}
