package com.gdgswu.planeat.domain.user.intolerance;

import com.gdgswu.planeat.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.gdgswu.planeat.global.exception.ErrorCode.INVALID_INTOLERANCE_NAME;

@RequiredArgsConstructor
@Getter
public enum IntoleranceEnum {
    EGG("Egg"),
    MILK("Milk"),
    BUCKWHEAT("Buckwheat"),
    PEANUT("Peanut"),
    SOYBEAN("Soybean"),
    WHEAT("Wheat"),
    PINE_NUT("Pine nut"),
    WALNUT("Walnut"),
    CRAB("Crab"),
    SHRIMP("Shrimp"),
    SQUID("Squid"),
    MACKEREL("Mackerel"),
    SHELLFISH("Shellfish"),
    PEACH("Peach"),
    TOMATO("Tomato"),
    CHICKEN("Chicken"),
    PORK("Pork"),
    BEEF("Beef"),
    SULFUROUS_ACID("Sulfurous acid");

    private final String name;

    public static IntoleranceEnum fromName(String dbValue) {
        return Arrays.stream(values())
                .filter(e -> e.name.equals(dbValue))
                .findFirst()
                .orElseThrow(() -> new CustomException(INVALID_INTOLERANCE_NAME));
    }
}
