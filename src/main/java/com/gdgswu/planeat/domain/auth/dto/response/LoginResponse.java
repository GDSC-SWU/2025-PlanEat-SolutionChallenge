package com.gdgswu.planeat.domain.auth.dto.response;

import lombok.Builder;

@Builder
public class LoginResponse {
    private String jwt;
    private Long userId;
}
