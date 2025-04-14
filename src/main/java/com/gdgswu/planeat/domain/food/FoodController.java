package com.gdgswu.planeat.domain.food;

import com.gdgswu.planeat.domain.food.dto.FoodResponse;
import com.gdgswu.planeat.global.response.ApiResponse;
import com.gdgswu.planeat.global.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FoodResponse>>> getFoodsByKeyword(
            @RequestParam String keyword,
            @PageableDefault(page = 1, size = 10) Pageable pageable
    ) {
        Page<FoodResponse> result = foodService.getFoods(keyword, pageable);
        return ResponseFactory.ok(result);
    }

    @GetMapping("/{food-id}")
    public  ResponseEntity<ApiResponse<FoodResponse>> getFoodById(@PathVariable("food-id") String id) {
        return ResponseFactory.ok(foodService.getFood(id));
    }
}
