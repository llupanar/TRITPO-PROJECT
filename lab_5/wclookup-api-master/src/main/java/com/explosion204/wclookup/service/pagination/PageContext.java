package com.explosion204.wclookup.service.pagination;

import com.explosion204.wclookup.exception.InvalidPageContextException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static com.explosion204.wclookup.exception.InvalidPageContextException.ErrorType.INVALID_PAGE_NUMBER;
import static com.explosion204.wclookup.exception.InvalidPageContextException.ErrorType.INVALID_PAGE_SIZE;

public class PageContext {
    private static final int MIN_PAGE = 1;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final int page;
    private final int pageSize;

    private PageContext(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public static PageContext of(Integer page, Integer pageSize) {
        if (page == null) {
            page = DEFAULT_PAGE;
        }

        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (page < MIN_PAGE) {
            throw new InvalidPageContextException(INVALID_PAGE_NUMBER, page);
        }

        if (pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) {
            throw new InvalidPageContextException(INVALID_PAGE_SIZE, pageSize);
        }

        return new PageContext(page, pageSize);
    }

    public PageRequest toPageRequest() {
        return toPageRequest(Sort.unsorted());
    }

    public PageRequest toPageRequest(Sort sort) {
        return PageRequest.of(page - 1, pageSize, sort);
    }
}
