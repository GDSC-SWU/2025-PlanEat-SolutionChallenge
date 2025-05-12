package com.gdgswu.planeat.domain.history;

import com.gdgswu.planeat.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class History {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "hitory_id")
    private Long id;

    private Long userId;

    private Long mainFoodId;
    private Long sideFoodId1;
    private Long sideFoodId2;

    private String recommendReason;

    private Double totalCalories;
    private Double totalCarbs;
    private Double totalProtein;
    private Double totalFat;

    @CreatedDate
    private LocalDateTime createdAt;
}
