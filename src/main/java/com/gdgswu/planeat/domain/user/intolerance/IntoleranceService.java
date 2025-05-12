package com.gdgswu.planeat.domain.user.intolerance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class IntoleranceService {
    private final IntoleranceRepository intoleranceRepository;

    @PostConstruct
    public void setIntoleranceTable() {
        if (intoleranceRepository.count() == 0){
            for (IntoleranceEnum enumValue : IntoleranceEnum.values()) {
                intoleranceRepository.save(new Intolerance(enumValue));
            }
        }
    }
}