package com.gdgswu.planeat.domain.admin;

import com.gdgswu.planeat.domain.food.FoodDataImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final FoodDataImportService foodDataImportService;

    @PostMapping("/admin/import-food-datas")
    public ResponseEntity<Void> importFoods() {
        foodDataImportService.loadData();
        return ResponseEntity.ok().build();
    }

}
