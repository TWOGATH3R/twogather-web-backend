package com.twogather.twogatherwebbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateResponse;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreOwnerServiceTest {
    @Mock
    private StoreOwnerRepository storeOwnerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberService memberService;
    private StoreOwnerService storeOwnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storeOwnerService = new StoreOwnerService(storeOwnerRepository, memberService, passwordEncoder);
    }
    @Test
    @DisplayName("save: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void save_ValidMemberSaveRequest_ShouldReturnTrue() {
        // given
        final MemberSaveRequest request = returnRequest();
        doNothing().when(memberService).checkMemberOverlap(any());
        final StoreOwner storeOwner = requestToEntity(request);
        when(storeOwnerRepository.save(any(StoreOwner.class))).thenReturn(storeOwner);
        // when
        storeOwnerService.join(request);

        // then
        Assertions.assertTrue(true);
    }
    private MemberSaveRequest returnRequest(){
        return new MemberSaveRequest(
                "testid1",
                "test@test.com",
                "test131",
                "김사업"
        );
    }
    private StoreOwner requestToEntity(MemberSaveRequest request){
        return new StoreOwner(
                request.getUsername(),
                request.getEmail(), request.getPassword(), request.getName(),
                AuthenticationType.STORE_OWNER, true);
    }

}
