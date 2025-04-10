package com.gdgswu.planeat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
    USER_ALREADY_EXISTS(BAD_REQUEST, "이미 가입된 사용자입니다."),
    ID_TOKEN_INVALID(BAD_REQUEST, "유효하지 않은 ID 토큰입니다."),
    INVALID_FOOD_ID(BAD_REQUEST, "유효하지 않은 음식 ID가 포함되어 있습니다."),

    // 401 Unauthorized
    SIGNUP_REQUIRED(UNAUTHORIZED, "회원가입이 필요합니다."),

    // 404 Not Found
    USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다."),
    HISTORY_NOT_FOUND(NOT_FOUND, "식단 추천 기록이 존재하지 않습니다."),

    // 500 Internal Server Error
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
