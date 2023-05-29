package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreOwnerServiceTest {
    @Mock
    private StoreOwnerRepository storeOwnerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
    private StoreOwnerService storeOwnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storeOwnerService = new StoreOwnerService(storeOwnerRepository, memberRepository, passwordEncoder);
    }
    @Test
    @DisplayName("save: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void save_ValidMemberSaveRequest_ShouldReturnTrue() {
        // given
        final MemberSaveUpdateRequest request = returnRequest();
        when(memberRepository.existsByUsername(request.getUsername())).thenReturn(false);
        final StoreOwner storeOwner = requestToEntity(request);
        when(storeOwnerRepository.save(any(StoreOwner.class))).thenReturn(storeOwner);
        // when
        storeOwnerService.join(request);

        // then
        Assertions.assertTrue(true);
    }
    @Test
    @DisplayName("save: 중복되는 이메일로 두번 가입했을때 예외가 터진다")
    public void save_DuplicateEmail_ShouldThrowMemberException() {
        // given
        final MemberSaveUpdateRequest request = returnRequest();
        //when
        when(memberRepository.existsByUsername(request.getUsername())).thenReturn(true);
        // when
        Assertions.assertThrows(MemberException.class, () -> storeOwnerService.join(request));
    }
    private MemberSaveUpdateRequest returnRequest(){
        return new MemberSaveUpdateRequest(
                "testid1",
                "test@test.com",
                "test131",
                "김사업"
        );
    }
    private StoreOwner requestToEntity(MemberSaveUpdateRequest request){
        return new StoreOwner(
                request.getUsername(),
                request.getEmail(), request.getPassword(), request.getName(),
                AuthenticationType.STORE_OWNER, true);
    }
}
