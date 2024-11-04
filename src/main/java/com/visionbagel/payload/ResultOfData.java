package com.visionbagel.payload;

public class ResultOfData<T> {
    public String message = "ok";

    public Long code = 0L;
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
