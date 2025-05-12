package com.gdgswu.planeat.domain.home;

import com.gdgswu.planeat.global.response.ApiResponse;
import com.gdgswu.planeat.global.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponse>> getHomeInformation() {
        return ResponseFactory.ok(homeService.getHomeInfo());
    }
}
