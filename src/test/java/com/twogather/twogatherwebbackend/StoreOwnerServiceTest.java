package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private StoreOwnerService storeOwnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storeOwnerService = new StoreOwnerService(storeOwnerRepository, validator);
    }
    @Test
    public void save_ValidMemberSaveRequest_ShouldReturnTrue() {
        // given
        final StoreOwnerSaveRequest request = returnRequest();
        when(validator.validateBizRegNumber(request.getBusinessNumber(),request.getBusinessStartDate(),request.getBusinessName())).thenReturn(true);
        when(storeOwnerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        final StoreOwner storeOwner = requestToEntity(request);
        when(storeOwnerRepository.save(any(StoreOwner.class))).thenReturn(storeOwner);
        // when
        storeOwnerService.save(request);

        // then
        Assertions.assertTrue(true);
    }
    @Test
    public void save_DuplicateEmail_ShouldThrowMemberException() {
        // given
        final StoreOwnerSaveRequest request = returnRequest();
        //when
        when(storeOwnerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when
        Assertions.assertThrows(MemberException.class, () -> storeOwnerService.save(request));
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
                request.getBusinessNumber(), request.getBusinessName(), stringToLocalDate(request.getBusinessStartDate()));
    }
    private LocalDate stringToLocalDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
