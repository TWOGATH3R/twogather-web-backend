package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.exception.UserException;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreOwnerServiceTest {
    @Mock
    private StoreOwnerRepository storeOwnerRepository;
    @Mock
    private BizRegNumberValidator validator;
    @InjectMocks
    private StoreOwnerService storeOwnerService;

    @Test
    public void save_ValidMemberSaveRequest_ShouldReturnTrue() {
        // given
        final StoreOwnerSaveRequest request = returnRequest();
        when(validator.validateBizRegNumber(request.getBusinessNumber(),request.getBusinessStartDate(),request.getBusinessName())).thenReturn(true);
        when(storeOwnerRepository.existsByEmail(request.getEmail())).thenReturn(false);

        // when
        storeOwnerService.save(request);

        // then
        Assertions.assertTrue(true);
    }
    @Test
    public void save_DuplicateEmail_ShouldThrowUserException() {
        // given
        final StoreOwnerSaveRequest request = returnRequest();
        //when
        when(storeOwnerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when
        Assertions.assertThrows(UserException.class, () -> storeOwnerService.save(request));
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
}
