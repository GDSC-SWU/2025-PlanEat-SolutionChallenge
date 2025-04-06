package com.gdgswu.planeat.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gdgswu.planeat.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private String code; // 실패할 경우
    private T data; // 성공할 경우

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "요청 성공");
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }
}
