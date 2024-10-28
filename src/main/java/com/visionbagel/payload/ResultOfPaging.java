package com.visionbagel.payload;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.hibernate.query.Query;

import java.util.List;

public class ResultOfPaging<T extends PanacheEntityBase> {
    public long currentPageNumber;
    public long lastPageNumber;
    public long pageSize;
    public long totalRecords;
    public String message = "ok";

    public Long code = 0L;
    public List<T> records;

    public ResultOfPaging(PanacheQuery<T> panacheQuery, Page page) {
        PanacheQuery<T> query = panacheQuery.page(page.index - 1, page.size);
        currentPageNumber = page.index;
        lastPageNumber = query.pageCount();
        pageSize = query.count();
        totalRecords = panacheQuery.count();
        records = query.list();
    }

    public ResultOfPaging<T> code(long code) {
        this.code = code;
        return this;
    }

    public ResultOfPaging<T> message(String message) {
        this.message = message;
        return this;
    }
}
