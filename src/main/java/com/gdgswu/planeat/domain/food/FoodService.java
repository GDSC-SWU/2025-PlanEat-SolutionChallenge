package com.gdgswu.planeat.domain.food;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgswu.planeat.domain.food.dto.FoodSearchResponse;
import com.gdgswu.planeat.domain.food.dto.FoodResponse;
import com.gdgswu.planeat.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.gdgswu.planeat.global.exception.ErrorCode.INTERNAL_ERROR;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient = new OkHttpClient();

    @Value("${api.serviceKey}")
    private String serviceKey;

    public List<FoodSearchResponse> getFoods(String keyword, int page, int size) {

        try {
            String url = "https://api.spoonacular.com/food/ingredients/search"
                    + "?query=" + keyword
                    + "&number=" + size
                    + "&offset=" + (page - 1) * size
                    + "&apiKey=" + serviceKey;
            System.out.println(url);

            Request request = buildRequestWith(url);

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return List.of();
                }

                JsonNode root = objectMapper.readTree(response.body().string());
                JsonNode items = root.path("results");
                System.out.println(items);

                if (items.isMissingNode() || !items.isArray()) return List.of();

                List<FoodSearchResponse> result = new ArrayList<>();
                for (JsonNode item : items) {
                    FoodSearchResponse food = FoodSearchResponse.builder()
                            .id(item.path("id").asLong())
                            .name(item.path("name").asText())
                            .build();
                    result.add(food);
                }

                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException("exception while calling api");
        }
    }

    public FoodResponse getFood(Long id) {
        if (foodRepository.existsById(id)) {
            return FoodResponse.from(foodRepository.findById(id).get());
        }
        try {
            String url = "https://api.spoonacular.com/food/ingredients/"
                    + id
                    + "/information"
                    + "?amount=1"
                    + "&unit=piece"
                    + "&apiKey=" + serviceKey;

            Request request = buildRequestWith(url);

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new CustomException(INTERNAL_ERROR);
                }
                JsonNode root = objectMapper.readTree(response.body().string());
                FoodResponse foodResponse = FoodResponse.builder()
                        .id(root.path("id").asLong())
                        .name(root.path("name").asText())
                        .imageUrl("https://img.spoonacular.com/ingredients_100x100/" + root.path("image").asText())
                        .category(root.path("categoryPath").toString())
                        .cost(root.path("estimatedCost").path("value").asDouble())
                        .costUnit(root.path("estimatedCost").path("unit").asText())
                        .weightPerServing(root.path("nutrition").path("weightPerServing").path("amount").asText())
                        .build();

                JsonNode nutrition = root.path("nutrition");

                foodResponse.setWeightPerServing(
                        nutrition.path("weightPerServing").path("amount").asText()
                + " " + nutrition.path("weightPerServing").path("unit").asText()
                );

                for (JsonNode nutrient : nutrition.path("nutrients")) {
                    switch (nutrient.path("name").asText()) {
                        case "Calories" -> foodResponse.setCalories(nutrient.path("amount").asInt(0));
                        case "Fat" -> foodResponse.setFat(nutrient.path("amount").asDouble(0.0));
                        case "Carbohydrates" -> foodResponse.setCarbs(nutrient.path("amount").asDouble(0.0));
                        case "Protein" -> foodResponse.setProtein(nutrient.path("amount").asDouble(0.0));
                    }
                }
                foodRepository.save(foodResponse.toEntity()); // cache
                return foodResponse;
            }
        } catch (IOException e) {
            throw new CustomException(INTERNAL_ERROR);
        }
    }

    private Request buildRequestWith(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .build();
    }

}