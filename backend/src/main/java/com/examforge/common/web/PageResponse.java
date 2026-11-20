package com.examforge.common.web;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/** Stable JSON shape for every paginated list endpoint. */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, String sort) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                describe(page.getSort()));
    }

    public static <E, T> PageResponse<T> from(Page<E> page, Function<? super E, ? extends T> mapper) {
        return from(page.map(mapper::apply));
    }

    private static String describe(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return null;
        }
        return sort.stream()
                .map(order ->
                        order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .collect(Collectors.joining(";"));
    }
}
