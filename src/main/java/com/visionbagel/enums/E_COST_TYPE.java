package com.visionbagel.enums;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public enum E_COST_TYPE {
    @Schema(description = "消费")
    WOMEN("decrement"),

    @Schema(description = "充值")
    MAN("increment");

    public final String label;

    private E_COST_TYPE(String label) {
        this.label = label;
    }
}
