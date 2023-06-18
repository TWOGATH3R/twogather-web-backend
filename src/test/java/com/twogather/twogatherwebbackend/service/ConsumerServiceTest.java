package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveRequest;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConsumerServiceTest {
    @Mock
    private ConsumerRepository consumerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberService memberService;

    private ConsumerService consumerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consumerService = new ConsumerService(consumerRepository, passwordEncoder,memberService);
    }

    @Test
    @DisplayName("save: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void save_ValidMemberSaveRequest_ShouldReturnTrue() {
        // given
        final MemberSaveRequest request = returnRequest();
        doNothing().when(memberService).checkMemberOverlapBySave(any());
        final Consumer consumer = requestToEntity(request);
        when(consumerRepository.save(any(Consumer.class))).thenReturn(consumer);

        // when
        consumerService.join(request);

        // then
        Assertions.assertTrue(true);
    }
    private Consumer requestToEntity(MemberSaveRequest request){
        return new Consumer(
                request.getUsername(),
                request.getEmail(), request.getPassword(), request.getName(),AuthenticationType.CONSUMER, true);
    }
    private MemberSaveRequest returnRequest(){
        return new MemberSaveRequest(
                "testid1",
                "test@test.com",
                "test",
                "김사업"
        );
    }
}
