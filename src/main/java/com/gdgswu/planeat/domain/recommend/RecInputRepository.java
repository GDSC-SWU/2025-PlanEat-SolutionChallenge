package com.gdgswu.planeat.domain.recommend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecInputRepository extends JpaRepository<RecInput, Long> {
}
