package com.gdgswu.planeat.domain.history;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistoryResponse {
    private Long historyId;
    private LocalDateTime createdAt;
}
