package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourUpdateRequest;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/business-hours")
@RequiredArgsConstructor
public class BusinessHourController {
    private final BusinessHourService businessHourService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Response> save(@RequestBody @Valid final BusinessHourSaveRequest businessHourSaveRequest) {
        BusinessHourResponse data = businessHourService.save(businessHourSaveRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }
    @PutMapping("/{businessHourId}")
    public ResponseEntity<Response> update(@PathVariable Long businessHourId, @RequestBody @Valid BusinessHourUpdateRequest businessHourUpdateRequest) {
        BusinessHourResponse data = businessHourService.update(businessHourId, businessHourUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @DeleteMapping("/{businessHourId}")
    public ResponseEntity<Void> delete(@PathVariable Long businessHourId) {
        businessHourService.delete(businessHourId);

        return ResponseEntity.noContent().build();
    }

}
