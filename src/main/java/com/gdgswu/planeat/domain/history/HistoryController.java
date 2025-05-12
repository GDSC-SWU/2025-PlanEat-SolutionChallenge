package com.gdgswu.planeat.domain.history;

import com.gdgswu.planeat.global.response.ApiResponse;
import com.gdgswu.planeat.global.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/histories")
@RestController
public class HistoryController {
    private final HistoryService historyService;

    @PostMapping
    public ResponseEntity<ApiResponse<HistoryResponse>> saveHistory(@RequestBody HistoryRequest request){
        return ResponseFactory.ok(historyService.save(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HistoryResponse>>> getUserHistories(
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
            @RequestParam(required = false, defaultValue = "createdAt", value = "criteria") String criteria
    ){
        return ResponseFactory.ok(historyService.getHistories(pageNo, criteria));
    }

    @GetMapping("/{history-id}")
    public ResponseEntity<ApiResponse<HistoryDetailResponse>> getHistoryDetail(@PathVariable("history-id") Long id){
        return ResponseFactory.ok(historyService.getHistoryById(id));
    }

}
