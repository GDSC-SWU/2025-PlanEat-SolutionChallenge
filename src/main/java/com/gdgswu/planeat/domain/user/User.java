package com.gdgswu.planeat.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.lang.annotation.Target;

@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User {
    @Id @Column(name = "user_id")
    @GeneratedValue
    private Long id;

    private String email;
    private String name;
    private int age;
    private int height;
    private int weight;
    private String location;

    @CreatedDate
    private String joinedAt;
    @LastModifiedDate
    private String updatedAt;
}
