package com.gdgswu.planeat.domain.home;

import com.gdgswu.planeat.domain.history.HistoryResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeResponse {
    private String userName;

    private Double todayTotalCal;
    private Double todayTotalCarb;
    private Double todayTotalProtein;
    private Double todayTotalFat;

    private List<HistoryResponse> todayHistories;
}
