package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourIdList;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourUpdateRequest;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/business-hours")
@RequiredArgsConstructor
public class BusinessHourController {
    private final BusinessHourService businessHourService;
    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> saveList(@PathVariable final Long storeId, @RequestBody @Valid final BusinessHourSaveListRequest request) {
        List<BusinessHourResponse> data = businessHourService.saveList(storeId, request.getBusinessHourSaveList());

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }
    @PutMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> updateList(@PathVariable final Long storeId, @RequestBody @Valid final BusinessHourUpdateListRequest request) {
        List<BusinessHourResponse> data = businessHourService.updateList(request.getBusinessHourUpdateList());

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @GetMapping
    public ResponseEntity<Response> getBusinessHourInfo(@PathVariable final Long storeId){
        List<BusinessHourResponse> data = businessHourService.findBusinessHoursByStoreId(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Void> deleteList(@PathVariable final Long storeId, @RequestBody final BusinessHourIdList businessHourIdList) {
        businessHourService.deleteList(businessHourIdList.getBusinessHourId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    //응답과 다르게 요청시에 온 데이터는 실제 BusinessHourSaveRequest의 리스트가 맞는지에 대한 검증작업도 해주어야하기때문에
    //도메인별로 class 생성
    public static class BusinessHourSaveListRequest {
        @Valid
        private List<BusinessHourSaveRequest> businessHourSaveList;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BusinessHourUpdateListRequest {
        @Valid
        private List<BusinessHourUpdateRequest> businessHourUpdateList;
    }

}
