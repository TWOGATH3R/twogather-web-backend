package com.twogather.twogatherwebbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@Getter
public class PagedResponse<T> extends Response{
    private int currentPage;//현재 페이지 번호
    private int totalPages;//전체 페이지 수
    private long totalElements;//전체 데이터 개수
    private int pageSize;//한 페이지의 데이터개수
    private boolean isFirst;//현재페이지가 첫 페이지인가?
    private boolean isLast;//현재페이지가 마지막 페이지인가?

    public PagedResponse(Page<T> page) {
        super(page.getContent());
        this.currentPage = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
        this.pageSize = page.getSize();
    }
}
