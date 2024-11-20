package com.visionbagel.payload;


import jakarta.validation.constraints.NotNull;

public class TopUpBody {
    @NotNull()
    public int money;

    public static String check(String data) {
        return switch (data) {
            case "1", "20", "40", "100", "200" -> "";
            default -> "请支持充值金额 (20、40、100、200)";
        };
    }
}
