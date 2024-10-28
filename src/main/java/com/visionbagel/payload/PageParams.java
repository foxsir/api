package com.visionbagel.payload;

import io.quarkus.panache.common.Page;
import jakarta.ws.rs.QueryParam;

public class PageParams {
    private static final int miniSize = 20;
    private static final int maxSize = 40;

    @QueryParam("page")
    public int page;

    @QueryParam("pageSize")
    public int pageSize;

    public static Page of(PageParams pageParams) {
        int page = pageParams.page;
        int pageSize = pageParams.pageSize;
        if(pageParams.page < 1) {
            page = 1;
        }
        if(pageParams.pageSize < PageParams.miniSize || pageParams.pageSize > PageParams.maxSize) {
            pageSize = PageParams.miniSize;
        }
        return Page.of(page, pageSize);
    }
}
