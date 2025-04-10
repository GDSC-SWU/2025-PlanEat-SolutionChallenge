package com.gdgswu.planeat.domain.food;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Service
public class FoodDataImportService {

    private final FoodRepository foodRepository;

    @Value("classpath:csv/20250408_음식DB.csv")
    private Resource foodCsv;

    @Transactional
    public void loadData() {
        try (Reader reader = new InputStreamReader(foodCsv.getInputStream(), StandardCharsets.UTF_8)) {
            List<Food> foods = new ArrayList<>();
            for (CSVRecord record : getRecords(reader)) {
                String name = record.get("식품명");
                String category = record.get("식품대분류명");

                Integer calories = parseInteger(record.get("에너지(kcal)"));
                Double protein = parseDouble(record.get("단백질(g)"));
                Double fat = parseDouble(record.get("지방(g)"));
                Double carbs = parseDouble(record.get("탄수화물(g)"));
                Double portionWeight = parsePortionWeight(record.get("식품중량"));

                foods.add(Food.builder()
                        .name(name)
                        .category(category)
                        .calories(calories)
                        .protein(protein)
                        .fat(fat)
                        .carbs(carbs)
                        .portionWeight(portionWeight)
                        .build());
            }
            foodRepository.saveAll(foods);
        } catch (IOException e) {
            throw new RuntimeException("음식 데이터 로딩 실패", e);
        }
    }

    private List<CSVRecord> getRecords(Reader reader) throws IOException {
        return CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader)
                .getRecords();
    }

    private Double parseDouble(String value) {
        try {
            return (value == null || value.isBlank()) ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            return (value == null || value.isBlank()) ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Double parsePortionWeight(String value) {
        if (value == null) return null;
        value = value.replace("g", "").replace("G", "").trim();
        return parseDouble(value);
    }
}
