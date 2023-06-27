package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.dto.PagedResponse;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberUpdateRequest;
import com.twogather.twogatherwebbackend.dto.store.MyStoreResponse;
import com.twogather.twogatherwebbackend.dto.store.RejectReason;
import com.twogather.twogatherwebbackend.service.MemberService;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final StoreService storeService;
    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<Response> findAdminInfo(@PathVariable Long memberId){
        MemberResponse data = memberService.findMember(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @PutMapping("/{memberId}")
    public ResponseEntity<Response> updateAdminInfo(@PathVariable Long memberId,
                                                    @RequestBody @Valid final MemberUpdateRequest request){
        MemberResponse data = memberService.update(memberId, request);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @PatchMapping("/stores/approve/{storeId}")
    public ResponseEntity<Response> approveStore(@PathVariable Long storeId){
        storeService.approveStore(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PatchMapping("/stores/reject/{storeId}")
    public ResponseEntity<Response> rejectStore(@PathVariable Long storeId,
                                                @RequestBody RejectReason rejectReason){
        storeService.rejectStore(storeId, rejectReason);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/stores/{type}")
    public ResponseEntity<PagedResponse> getAllStore(@PathVariable StoreStatus type, Pageable pageable){
        Page<MyStoreResponse> response =  storeService.getStores(type, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(response));
    }
}
