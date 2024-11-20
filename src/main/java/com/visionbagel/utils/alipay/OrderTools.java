package com.visionbagel.utils.alipay;

import java.time.Instant;
import java.time.LocalDate;

public class OrderTools {
    public static String generateOrderNo() {
        Instant instant = Instant.now();

        LocalDate currentDate = LocalDate.now();
        return String.join(
            "",
            String.valueOf(currentDate.getYear()),
            String.valueOf(currentDate.getMonth().getValue()),
            String.valueOf(currentDate.getDayOfMonth()),
            String.valueOf(instant.getNano())
        );
    }
}
