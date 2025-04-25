package com.gdgswu.planeat.domain.user.intolerance;

import com.gdgswu.planeat.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.gdgswu.planeat.global.exception.ErrorCode.INVALID_INTOLERANCE_NAME;

@RequiredArgsConstructor
@Getter
public enum IntoleranceEnum {
    DAIRY("Dairy"),
    PEANUT("Peanut"),
    SOY("Soy"),
    EGG("Egg"),
    SEAFOOD("Seafood"),
    SULFITE("Sulfite"),
    GLUTEN("Gluten"),
    SESAME("Sesame"),
    TREE_NUT("Tree Nut"),
    GRAIN("Grain"),
    SHELLFISH("Shellfish"),
    WHEAT("Wheat");

    private final String name;

    public static IntoleranceEnum fromName(String dbValue) {
        return Arrays.stream(values())
                .filter(e -> e.name.equals(dbValue))
                .findFirst()
                .orElseThrow(() -> new CustomException(INVALID_INTOLERANCE_NAME));
    }
}
