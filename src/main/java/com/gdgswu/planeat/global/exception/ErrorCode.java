package com.gdgswu.planeat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_REQUEST(BAD_REQUEST, "Invalid request."),
    USER_ALREADY_EXISTS(BAD_REQUEST, "User already registered."),
    ID_TOKEN_INVALID(BAD_REQUEST, "Invalid ID token."),
    INVALID_FOOD_ID(BAD_REQUEST, "Invalid food ID."),

    // 401 Unauthorized
    SIGNUP_REQUIRED(UNAUTHORIZED, "Signup required."),

    // 404 Not Found
    USER_NOT_FOUND(NOT_FOUND, "User not found."),
    HISTORY_NOT_FOUND(NOT_FOUND, "No food recommendation history found."),

    // 500 Internal Server Error
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "An internal server error has occurred.");
    private final HttpStatus status;
    private final String message;
}
