package com.gdgswu.planeat.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GoogleUserInfo {
    private final String email;
}
