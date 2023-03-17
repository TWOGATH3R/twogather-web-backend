package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.dto.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.dto.ConsumerSaveResponse;
import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.exception.UserException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.service.ConsumerService;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConsumerServiceTest {
    @Mock
    private ConsumerRepository consumerRepository;

    private ConsumerService consumerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consumerService = new ConsumerService(consumerRepository);
    }

    @Test
    public void save_ValidMemberSaveRequest_ShouldReturnTrue() {
        // given
        final ConsumerSaveRequest request = returnRequest();
        when(consumerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        final Consumer consumer = requestToEntity(request);
        when(consumerRepository.save(any(Consumer.class))).thenReturn(consumer);

        // when
        consumerService.save(request);

        // then
        Assertions.assertTrue(true);
    }
    @Test
    public void save_DuplicateEmail_ShouldThrowUserException() {
        // given
        final ConsumerSaveRequest request = returnRequest();
        //when
        when(consumerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when
        Assertions.assertThrows(UserException.class, () -> consumerService.save(request));
    }
    private Consumer requestToEntity(ConsumerSaveRequest request){
        return new Consumer(request.getEmail(), request.getPassword(), request.getName(), request.getPhone());
    }
    private ConsumerSaveRequest returnRequest(){
        return new ConsumerSaveRequest(
                "test@test.com",
                "test",
                "김사업",
                "010-1234-1234"
        );
    }
}
