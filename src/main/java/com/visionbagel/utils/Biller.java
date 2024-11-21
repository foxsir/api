package com.visionbagel.utils;

import java.math.BigDecimal;

public class Biller {
    public static BigDecimal calc(long width, long height, String model) {
        return switch (model) {
            case "fal-ai/flux/dev" -> BigDecimal.valueOf((double) (width * height) / (1000 * 1000) * (0.025 * 7 * 1.8));
            case "fal-ai/flux-pro/v1.1-ultra" -> BigDecimal.valueOf((0.06 * 7 * 1.8));
            // case "fal-ai/flux-pro/new" -> BigDecimal.valueOf((double) (width * height) / (1000 * 1000) * (0.05 * 7 * 1.8));
            default -> BigDecimal.valueOf((double) (width * height) / (1000 * 1000) * (0.05 * 7 * 1.8));
        };
    }
}
