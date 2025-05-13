package com.gdgswu.planeat.domain.user;

import com.gdgswu.planeat.domain.user.dto.UserRequest;
import com.gdgswu.planeat.domain.user.dto.UserResponse;
import com.gdgswu.planeat.global.response.ApiResponse;
import com.gdgswu.planeat.global.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo() {
        return ResponseFactory.ok(userService.get());
    }

    @PutMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> editUserInfo(@RequestBody UserRequest request) {
        return ResponseFactory.ok(userService.edit(request));
    }

    @DeleteMapping("/info")
    public ResponseEntity<?> deleteUser() {
        userService.delete();
        return ResponseFactory.noContent();
    }
}
