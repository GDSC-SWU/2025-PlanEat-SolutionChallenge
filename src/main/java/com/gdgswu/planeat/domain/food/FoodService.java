package com.gdgswu.planeat.domain.food;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgswu.planeat.domain.food.dto.FoodResponse;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient = new OkHttpClient();

    @Value("${openapi.serviceKey}")
    private String serviceKey;

    public Page<FoodResponse> getFoods(String keyword, Pageable pageable) {
        int requiredPage = pageable.getPageNumber();
        int requiredSize = pageable.getPageSize();

        // first find in cache
        Page<Food> cachedPage = foodRepository.findByNameContaining(keyword, pageable);
        long totalCached = foodRepository.countByNameContaining(keyword);

        // if cached is enough
        if (totalCached >= (pageable.getPageNumber() + 1L) * requiredSize) {
            return cachedPage.map(FoodResponse::from);
        }

        // if not enough, calc amount
        int missing = (int) ((pageable.getPageNumber() + 1L) * requiredSize - totalCached);

        // call api (2 times of missing amount)
        List<Food> fetched = fetchFromApi(keyword, requiredPage, missing * 2);
        List<String> ids = fetched.stream().map(Food::getId).toList();
        Set<String> existingIds = foodRepository.findAllById(ids).stream()
                .map(Food::getId)
                .collect(Collectors.toSet());

        List<Food> newFoods = fetched.stream()
                .filter(f -> !existingIds.contains(f.getId()))
                .toList();

        // save
        foodRepository.saveAll(newFoods);

        // return final result
        Page<Food> finalResult = foodRepository.findByNameContaining(keyword, pageable);
        return finalResult.map(FoodResponse::from);
    }

    public FoodResponse getFood(String id) {
        Food food = foodRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_FOOD_ID));
        return FoodResponse.from(food);
    }

    private List<Food> fetchFromApi(String keyword, int pageNo, int size) {
        try {
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            String url = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02"
                    + "?serviceKey=" + encodedServiceKey
                    + "&FOOD_NM_KR=" + encodedKeyword
                    + "&pageNo=" + pageNo
                    + "&numOfRows=" + size
                    + "&type=json";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return List.of();
                }

                String body = response.body().string();

                JsonNode root = objectMapper.readTree(body);
                JsonNode items = root.path("body").path("items");

                if (items.isMissingNode() || !items.isArray()) return List.of();

                List<Food> result = new ArrayList<>();
                for (JsonNode item : items) {
                    Food food = Food.builder()
                            .id(item.path("FOOD_CD").asText())
                            .name(item.path("FOOD_NM_KR").asText())
                            .calories(item.path("AMT_NUM1").asInt(0))
                            .carbs(item.path("AMT_NUM6").asDouble(0.0))
                            .protein(item.path("AMT_NUM3").asDouble(0.0))
                            .fat(item.path("AMT_NUM4").asDouble(0.0))
                            .portionWeight(item.path("SERVING_SIZE").asText(""))
                            .build();
                    result.add(food);
                }

                return result;
            }
        } catch (Exception e) {
            return List.of();
        }
    }
}