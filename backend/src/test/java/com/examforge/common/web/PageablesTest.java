package com.examforge.common.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.examforge.common.exception.BadRequestException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class PageablesTest {

    private static final Set<String> ALLOWED = Set.of("createdAt", "title");
    private static final Sort DEFAULT = Sort.by(Sort.Direction.DESC, "createdAt");

    @Test
    void appliesDefaultSortWhenUnsorted() {
        Pageable result = Pageables.sanitize(PageRequest.of(2, 10), ALLOWED, DEFAULT);

        assertThat(result.getPageNumber()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getSort()).isEqualTo(DEFAULT);
    }

    @Test
    void keepsAllowedSort() {
        Pageable result = Pageables.sanitize(PageRequest.of(0, 20, Sort.by("title")), ALLOWED, DEFAULT);

        assertThat(result.getSort()).isEqualTo(Sort.by("title"));
    }

    @Test
    void rejectsUnknownSortProperty() {
        assertThatThrownBy(() -> Pageables.sanitize(PageRequest.of(0, 20, Sort.by("passwordHash")), ALLOWED, DEFAULT))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("passwordHash")
                .extracting("code")
                .isEqualTo("INVALID_SORT");
    }

    @Test
    void pageResponseDescribesSortAndTotals() {
        var page =
                new PageImpl<>(List.of("a", "b"), PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "createdAt")), 5);

        PageResponse<String> response = PageResponse.from(page, String::toUpperCase);

        assertThat(response.content()).containsExactly("A", "B");
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(5);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.sort()).isEqualTo("createdAt,desc");
    }
}
