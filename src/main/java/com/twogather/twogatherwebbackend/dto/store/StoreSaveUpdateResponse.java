package com.twogather.twogatherwebbackend.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

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
    private Long categoryId;

}
