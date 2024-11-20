package com.visionbagel.payload;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class ResultOfData<T> {
    @Schema(required = true)
    public String message = "ok";

    @Schema(required = true)
    public Long code = 200L;
    public T data = null;

    public ResultOfData(T data) {
        this.data = data;
    }

    public ResultOfData() {}

    public ResultOfData<T> code(long code) {
        this.code = code;
        return this;
    }

    public ResultOfData<T> message(String message) {
        this.message = message;
        return this;
    }
}
