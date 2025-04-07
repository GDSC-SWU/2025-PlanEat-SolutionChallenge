package com.gdgswu.planeat.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class LoginResponse {
    private String jwt;
    private Long userId;
}
