package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveResponse;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.net.URI;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class StoreOwnerController {
    private final StoreOwnerService storeOwnerService;
    @PostMapping
    public ResponseEntity<Void> join(@RequestBody @Valid final StoreOwnerSaveRequest storeOwnerSaveRequest) {
        StoreOwnerSaveResponse memberSaveResponse = storeOwnerService.save(storeOwnerSaveRequest);

        return ResponseEntity.created(URI.create("/api/owner/" + memberSaveResponse.getId())).build();
    }

    @GetMapping
    public ResponseEntity<Void> validateEmail(
            @RequestParam
            @NotBlank(message = "비어있는 항목을 입력해주세요.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            final String email) {
        storeOwnerService.validateDuplicateEmail(email);
        return ResponseEntity.ok().build();
    }
}
