package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.comment.CommentSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.keyword.KeywordResponse;
import com.twogather.twogatherwebbackend.repository.KeywordRepository;
import com.twogather.twogatherwebbackend.service.KeywordService;
import com.twogather.twogatherwebbackend.service.StoreKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordController {
    private final KeywordService keywordService;
    private final StoreKeywordService storeKeywordService;

    @GetMapping
    public ResponseEntity<Response> getNKeywordList(@RequestParam(defaultValue = "5") int count) {
        List<KeywordResponse> data = keywordService.getNKeywordList(count);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @PostMapping("/stores/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> setStoreKeywordAssociation(@PathVariable Long storeId,
                                                                @RequestBody final List<String> keywordList){
        storeKeywordService.setStoreKeyword(storeId, keywordList);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
