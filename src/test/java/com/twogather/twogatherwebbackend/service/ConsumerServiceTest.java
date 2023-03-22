package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("save: 유효한 요청이 왔을때 유효한 응답을 반환한다")
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
    @DisplayName("save: 중복된 이메일로 저장요청이 왔을때 예외를 반환한다")
    public void save_DuplicateEmail_ShouldThrowMemberException() {
        // given
        final ConsumerSaveRequest request = returnRequest();
        //when
        when(consumerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when
        Assertions.assertThrows(MemberException.class, () -> consumerService.save(request));
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
