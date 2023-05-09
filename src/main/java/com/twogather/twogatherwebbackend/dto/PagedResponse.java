package com.twogather.twogatherwebbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@Getter
public class PagedResponse<T> extends Response{
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean isFirst;
    private boolean isLast;

    public PagedResponse(Page<T> page) {
        super(page.getContent());
        this.currentPage = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
    }
}
