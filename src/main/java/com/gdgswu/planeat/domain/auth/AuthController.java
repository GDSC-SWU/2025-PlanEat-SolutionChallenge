package com.gdgswu.planeat.domain.auth;

import com.gdgswu.planeat.domain.auth.dto.request.LoginRequest;
import com.gdgswu.planeat.domain.auth.dto.request.SignupRequest;
import com.gdgswu.planeat.domain.auth.dto.response.LoginResponse;
import com.gdgswu.planeat.domain.auth.service.AuthService;
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
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginWithGoogle(@RequestBody LoginRequest request) {
        return ResponseFactory.ok(authService.login(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(@RequestBody SignupRequest request) {
        return ResponseFactory.ok(authService.signup(request));
    }
}
