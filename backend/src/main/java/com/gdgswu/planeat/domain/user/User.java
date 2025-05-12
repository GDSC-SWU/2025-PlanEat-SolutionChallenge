package com.gdgswu.planeat.domain.user;

import com.gdgswu.planeat.domain.user.dto.UserRequest;
import com.gdgswu.planeat.domain.user.intolerance.Intolerance;
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
    private String location;

    private Integer mealsPerDay;
    private Integer hungerCycle;
    private Boolean canCook;

    @CreatedDate
    private LocalDateTime joinedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "user_intolerances",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "intolerance_id")
    )
    @Builder.Default
    private Set<Intolerance> intolerances = new HashSet<>();

    public void updateInfo(UserRequest request, Set<Intolerance> intolerances) {
        this.name = request.getName();
        this.gender = request.getGender();
        this.age = request.getAge();
        this.height = request.getHeight();
        this.weight = request.getWeight();
        this.location = request.getLocation();
        this.mealsPerDay = request.getMealsPerDay();
        this.hungerCycle = request.getHungerCycle();
        this.canCook = request.getCanCook();
        this.intolerances = intolerances;
    }

}
