package com.banreservas.customer.presentation.dto;

import com.banreservas.customer.domain.model.PagedResult;

import java.util.List;

public class PagedResponse<T> {

    public List<T> data;
    public int page;
    public int size;
    public long total;
    public int totalPages;

    public static <T> PagedResponse<T> from(PagedResult<T> result) {
        PagedResponse<T> r = new PagedResponse<>();
        r.data = result.data();
        r.page = result.page();
        r.size = result.size();
        r.total = result.total();
        r.totalPages = result.totalPages();
        return r;
    }
}
