package com.gdgswu.planeat.domain.food;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, String> {
    Page<Food> findByNameContaining(String keyword, Pageable pageable);
    long countByNameContaining(String keyword);
    boolean existsById(String id);
}
