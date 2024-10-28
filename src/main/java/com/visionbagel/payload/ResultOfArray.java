package com.visionbagel.payload;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import java.util.List;

@Deprecated
public class ResultOfArray {

    public int currentPageNumber;
    public int lastPageNumber;
    public int pageSize;
    public long totalRecords;
    public String message;
    public List<?> records;
    public int code = 0;


    public static class Builder {
        public ResultOfArray build() {
            return new ResultOfArray(this);
        }

        public Builder data(List<?> records) {
            this.records = records;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public int currentPageNumber;
        public int lastPageNumber;
        public int pageSize;
        public long totalRecords;
        public String message;
        public List<?> records;
        public int code = 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    private ResultOfArray(Builder builder) {
        currentPageNumber = builder.currentPageNumber;
        lastPageNumber = builder.lastPageNumber;
        pageSize = builder.pageSize;
        totalRecords = builder.totalRecords;
        message = builder.message;
        records = builder.records;
        code = builder.code;
    }
}
