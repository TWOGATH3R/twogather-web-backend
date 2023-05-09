package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourIdList;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourUpdateRequest;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BusinessHourController {
    private final BusinessHourService businessHourService;

    @PostMapping("/{storeId}/business-hours")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Response> saveList(@PathVariable final Long storeId, @RequestBody @Valid final List<BusinessHourSaveRequest> businessHourSaveRequestList) {
        List<BusinessHourResponse> data = businessHourService.saveList(storeId, businessHourSaveRequestList);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }
    @PutMapping("/business-hours")
    public ResponseEntity<Response> updateList(@RequestBody @Valid final List<BusinessHourUpdateRequest> businessHourUpdateRequestList) {
        List<BusinessHourResponse> data = businessHourService.updateList(businessHourUpdateRequestList);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @GetMapping("/{storeId}/business-hours")
    public ResponseEntity<Response> getBusinessHourInfo(@PathVariable final Long storeId){
        List<BusinessHourResponse> data = businessHourService.findBusinessHoursByStoreId(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @DeleteMapping("/business-hours")
    public ResponseEntity<Void> deleteList(@RequestBody final BusinessHourIdList businessHourIdList) {
        businessHourService.deleteList(businessHourIdList.getBusinessHourId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
