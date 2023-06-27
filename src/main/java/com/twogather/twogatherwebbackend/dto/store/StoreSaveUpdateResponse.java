package com.twogather.twogatherwebbackend.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twogather.twogatherwebbackend.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreSaveUpdateResponse {
    private Long storeId;
    private String storeName;
    private String address;
    private String phone;
    private String businessNumber;
    private String businessName;
    private LocalDate businessStartDate;
    private List<String> keywordList;
    private String categoryName;

    public static StoreSaveUpdateResponse of(Store store){
        return StoreSaveUpdateResponse
                .builder()
                .storeId(store.getStoreId())
                .storeName(store.getName())
                .address(store.getAddress())
                .phone(store.getPhone())
                .businessName(store.getBusinessName())
                .businessNumber(store.getBusinessNumber())
                .businessStartDate(store.getBusinessStartDate())
                .categoryName(store.getCategory().getName())
                .keywordList(store.getStoreKeywordList().stream().map(e->e.getKeyword().getName()).collect(Collectors.toList())).build();
    }

}
