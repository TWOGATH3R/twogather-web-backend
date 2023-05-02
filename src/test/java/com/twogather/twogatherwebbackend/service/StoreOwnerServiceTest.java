package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.dto.valid.BizRegNumberValidator;
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
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreOwnerServiceTest {
    @Mock
    private StoreOwnerRepository storeOwnerRepository;
    @Mock
    private BizRegNumberValidator validator;
    @Mock
    private PasswordEncoder passwordEncoder;
    private StoreOwnerService storeOwnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storeOwnerService = new StoreOwnerService(storeOwnerRepository, validator, passwordEncoder);
    }
    @Test
    @DisplayName("save: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void save_ValidMemberSaveRequest_ShouldReturnTrue() {
        // given
        final StoreOwnerSaveRequest request = returnRequest();
        when(validator.validateBizRegNumber(request.getBusinessNumber(),request.getBusinessStartDate(),request.getBusinessName())).thenReturn(true);
        when(storeOwnerRepository.existsByEmail(request.getEmail())).thenReturn(false);
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
        final StoreOwnerSaveRequest request = returnRequest();
        //when
        when(storeOwnerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when
        Assertions.assertThrows(MemberException.class, () -> storeOwnerService.join(request));
    }
    private StoreOwnerSaveRequest returnRequest(){
        return new StoreOwnerSaveRequest(
                "test@test.com",
                "test",
                "김사업",
                "010-1234-1234",
                "0000000000",
                "김사업이름",
                "20200101"
        );
    }
    private StoreOwner requestToEntity(StoreOwnerSaveRequest request){
        return new StoreOwner(request.getEmail(), request.getPassword(), request.getName(), request.getPhone(),
                request.getBusinessNumber(), request.getBusinessName(), stringToLocalDate(request.getBusinessStartDate()),
                AuthenticationType.OWNER, true);
    }
    private LocalDate stringToLocalDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
