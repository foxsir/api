package com.visionbagel.enums;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public enum E_COST_TYPE {
    @Schema(description = "消费")
    DECREMENT("decrement"),

    @Schema(description = "充值")
    INCREMENT("increment");

    public final String label;

    private E_COST_TYPE(String label) {
        this.label = label;
    }
}
