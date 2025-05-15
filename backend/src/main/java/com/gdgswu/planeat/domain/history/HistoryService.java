package com.gdgswu.planeat.domain.history;

import com.gdgswu.planeat.domain.user.User;
import com.gdgswu.planeat.domain.user.UserRepository;
import com.gdgswu.planeat.global.exception.CustomException;
import com.gdgswu.planeat.global.exception.ErrorCode;
import com.gdgswu.planeat.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.gdgswu.planeat.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class HistoryService {

    private static final int PAGE_SIZE = 5;

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    public HistoryResponse save(HistoryRequest request){
        User user = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        History history = historyRepository.save(History.builder()
                .userId(user.getId())
                .foodName(request.getFoodName())
                .foodImgUrl(request.getFoodImgUrl())
                .recommendReason(request.getRecommendReason())
                .totalCalories(request.getTotalCalories())
                .totalCarbs(request.getTotalCarbs())
                .totalProtein(request.getTotalProtein())
                .totalFat(request.getTotalFat())
                .build());

        return HistoryResponse.builder()
                .historyId(history.getId())
                .foodName(history.getFoodName())
                .foodImgUrl(history.getFoodImgUrl())
                .totalCalories(history.getTotalCalories())
                .createdAt(history.getCreatedAt())
                .build();
    }

    public List<HistoryResponse> getHistories(int pageNo, String criteria) {
        User user = userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<History> histories = historyRepository.findAllByUserId(user.getId(), pageable);

        List<HistoryResponse> responseList = new ArrayList<>(histories.getSize());
        for (History history : histories) {
            responseList.add(HistoryResponse.builder()
                            .historyId(history.getId())
                            .foodName(history.getFoodName())
                            .foodImgUrl(history.getFoodImgUrl())
                            .totalCalories(history.getTotalCalories())
                            .createdAt(history.getCreatedAt())
                            .build());
        }
        return responseList;
    }

    public HistoryDetailResponse getHistoryById(Long id) {
        History history = historyRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.HISTORY_NOT_FOUND));

        return HistoryDetailResponse.builder()
                .historyId(history.getId())
                .foodName(history.getFoodName())
                .foodImgUrl(history.getFoodImgUrl())
                .recommendReason(history.getRecommendReason())
                .totalCalories(history.getTotalCalories())
                .totalCarbs(history.getTotalCarbs())
                .totalProtein(history.getTotalProtein())
                .totalFat(history.getTotalFat())
                .createdAt(history.getCreatedAt())
                .build();
    }
}