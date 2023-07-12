package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.common.Response;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
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

    @PutMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> setBusinessHourList(@PathVariable final Long storeId, @RequestBody @Valid final BusinessHourSaveUpdateListRequest request) {
        List<BusinessHourResponse> data = businessHourService.setList(storeId, request.getBusinessHourList());

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping
    public ResponseEntity<Response> getBusinessHoursByStoreId(@PathVariable final Long storeId){
        List<BusinessHourResponse> data = businessHourService.findBusinessHoursByStoreId(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
}
