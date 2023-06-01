package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.menu.MenuIdList;
import com.twogather.twogatherwebbackend.dto.menu.MenuResponse;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveRequest;
import com.twogather.twogatherwebbackend.dto.menu.MenuUpdateRequest;
import com.twogather.twogatherwebbackend.service.MenuService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> saveList(@PathVariable final Long storeId, @RequestBody @Valid final MenuSaveListRequest request) {
        List<MenuResponse> data = menuService.saveList(storeId, request.getMenuSaveList());

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }
    @PatchMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> updateList(@PathVariable final Long storeId, @RequestBody @Valid final MenuUpdateListRequest request) {
        List<MenuResponse> data = menuService.updateList(storeId, request.getMenuUpdateList());

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @GetMapping
    public ResponseEntity<Response> getMenuByStore(@PathVariable final Long storeId){
        List<MenuResponse> data = menuService.findMenusByStoreId(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Void> deleteList(@PathVariable final Long storeId, @RequestBody final MenuIdList menuIdList) {
        menuService.deleteMenuByStoreIdAndMenuId(storeId, menuIdList.getMenuIdList());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MenuSaveListRequest {
        @Valid
        private List<MenuSaveRequest> menuSaveList;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MenuUpdateListRequest {
        @Valid
        private List<MenuUpdateRequest> menuUpdateList;
    }
}
