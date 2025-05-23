package com.gdgswu.planeat.domain.history;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class History {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "history_id")
    private Long id;

    private Long userId;

    private String foodName;
    private String foodImgUrl;

    @Column(length = 2000)
    private String recommendReason;

    private Double totalCalories;
    private Double totalCarbs;
    private Double totalProtein;
    private Double totalFat;

    @CreatedDate
    private LocalDateTime createdAt;
}