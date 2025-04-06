package com.gdgswu.planeat.domain.auth;

import com.gdgswu.planeat.domain.auth.dto.request.LoginRequest;
import com.gdgswu.planeat.domain.auth.dto.request.SignupRequest;
import com.gdgswu.planeat.domain.auth.dto.response.LoginResponse;
import com.gdgswu.planeat.domain.auth.service.OAuth2Service;
import com.gdgswu.planeat.global.response.ApiResponse;
import com.gdgswu.planeat.global.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginWithGoogle(@RequestBody LoginRequest request) {
        return ResponseFactory.ok(oAuth2Service.login(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(@RequestBody SignupRequest request) {
        return ResponseFactory.ok(oAuth2Service.signup(request));
    }
}
