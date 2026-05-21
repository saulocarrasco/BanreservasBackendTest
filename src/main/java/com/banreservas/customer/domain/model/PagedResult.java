package com.banreservas.customer.domain.model;

import java.util.List;
import java.util.function.Function;

public record PagedResult<T>(
    List<T> data,
    int page,
    int size,
    long total,
    int totalPages
) {
    public <R> PagedResult<R> map(Function<T, R> mapper) {
        return new PagedResult<>(data.stream().map(mapper).toList(), page, size, total, totalPages);
    }
}
