package com.gdgswu.planeat.domain.food;

import com.gdgswu.planeat.domain.food.dto.FoodSearchResponse;
import com.gdgswu.planeat.domain.food.dto.FoodResponse;
import com.gdgswu.planeat.global.response.ApiResponse;
import com.gdgswu.planeat.global.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodSearchResponse>>> getFoodsByKeyword(
            @RequestParam String keyword,
            @RequestParam Integer size
    ) {
        return ResponseFactory.ok(foodService.getFoods(keyword, size));
    }

    @GetMapping("/{food-id}")
    public  ResponseEntity<ApiResponse<FoodResponse>> getFoodById(@PathVariable("food-id") Long id) {
        return ResponseFactory.ok(foodService.getFood(id));
    }
}
