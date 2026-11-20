package com.examforge.common.web;

import com.examforge.common.exception.BadRequestException;
import com.examforge.common.exception.CommonErrorCodes;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Helpers that keep list endpoints safe: whitelisted sort fields and a default sort. */
public final class Pageables {

    private Pageables() {}

    /**
     * Rejects sort properties that are not in {@code allowed} (prevents sorting by arbitrary or
     * sensitive columns) and applies {@code defaultSort} when none was requested.
     */
    public static Pageable sanitize(Pageable pageable, Set<String> allowed, Sort defaultSort) {
        for (Sort.Order order : pageable.getSort()) {
            if (!allowed.contains(order.getProperty())) {
                throw new BadRequestException(
                        CommonErrorCodes.INVALID_SORT,
                        "Sorting by '" + order.getProperty() + "' is not supported. Allowed: " + allowed);
            }
        }
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : defaultSort;
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
