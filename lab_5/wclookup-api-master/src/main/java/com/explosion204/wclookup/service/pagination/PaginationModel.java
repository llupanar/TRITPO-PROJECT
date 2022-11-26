package com.explosion204.wclookup.service.pagination;

import com.explosion204.wclookup.service.dto.identifiable.IdentifiableDto;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginationModel<T extends IdentifiableDto> {
    private List<T> data;
    private int page;
    private int pageSize;
    private int totalPages;
    private long totalEntities;

    public static <T extends IdentifiableDto> PaginationModel<T> fromPage(Page<T> page) {
        PaginationModel<T> paginationModel = new PaginationModel<>();

        paginationModel.data = page.getContent();
        paginationModel.page = page.getNumber() + 1; // zero based
        paginationModel.pageSize = page.getSize();
        paginationModel.totalPages = page.getTotalPages();
        paginationModel.totalEntities = page.getTotalElements();

        return paginationModel;
    }
}
