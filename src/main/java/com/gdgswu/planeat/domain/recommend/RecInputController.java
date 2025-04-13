package com.gdgswu.planeat.domain.recommend;

import com.gdgswu.planeat.domain.recommend.dto.RecInputRequest;
import com.gdgswu.planeat.domain.recommend.dto.RecInputResponse;
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
@RequestMapping("/api/recommend-input")
public class RecInputController {

    private final RecInputService recInputService;

    @PostMapping()
    public ResponseEntity<ApiResponse<RecInputResponse>> saveRecommendInput(@RequestBody RecInputRequest request) {
        return ResponseFactory.ok(recInputService.save(request));
    }
}
