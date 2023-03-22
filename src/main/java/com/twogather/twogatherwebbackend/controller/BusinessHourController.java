package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.*;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/business-hour")
@RequiredArgsConstructor
public class BusinessHourController {
    private final BusinessHourService businessHourService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Validated @Valid final BusinessHourSaveRequest businessHourSaveRequest) {
        BusinessHourSaveResponse businessHourSaveResponse = businessHourService.save(businessHourSaveRequest);

        return ResponseEntity.created(URI.create("/api/business-hour/" + businessHourSaveResponse.getId())).build();
    }
    @PutMapping("/{businessHourId}")
    public ResponseEntity<Void> update(@PathVariable Long businessHourId, @RequestBody @Valid BusinessHourUpdateRequest businessHourUpdateRequest) {
        BusinessHourUpdateResponse businessHourUpdateResponse = businessHourService.update(businessHourId, businessHourUpdateRequest);

        return ResponseEntity.created(URI.create("/api/stores/" + businessHourUpdateResponse.getId())).build();
    }

    @DeleteMapping("/{businessHourId}")
    public ResponseEntity<Void> delete(@PathVariable Long businessHourId) {
        businessHourService.delete(businessHourId);

        return ResponseEntity.noContent().build();
    }
}
