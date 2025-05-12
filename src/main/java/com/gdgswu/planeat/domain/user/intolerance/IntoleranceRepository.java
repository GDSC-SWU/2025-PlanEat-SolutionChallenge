package com.gdgswu.planeat.domain.user.intolerance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IntoleranceRepository extends JpaRepository<Intolerance, Long> {
    List<Intolerance> findAllByNameIn(Collection<IntoleranceEnum> intoleranceNames);
}
