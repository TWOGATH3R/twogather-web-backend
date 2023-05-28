package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.LikeRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.EntityManager;

import static com.twogather.twogatherwebbackend.acceptance.TestHelper.createAuthority;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LikeAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityManager em;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Test
    @DisplayName("가게 좋아요를 눌렸을때 디비에 제대로된 관계를 맺으며 저장되었는지 확인")
    public void whenAddStoreLike_ThenSuccessAssociationMemberAndStoreAndLikes() throws Exception {
        //given
        Store store = storeRepository.save(new Store("가게이름", "주소", "010-1234-1234"));
        Consumer consumer =
                consumerRepository.save(new Consumer(
                        "email@naber.cim", passwordEncoder.encode("asdad1234"),
                        "kimminji", AuthenticationType.CONSUMER, true
                ));
        createAuthority(consumer);
        //when
        //then
        mockMvc.perform(
                post("/api/stores/{storeId}/likes", store.getStoreId()))
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Likes likes = likeRepository.findByStoreStoreIdAndMemberMemberId(store.getStoreId(), consumer.getMemberId()).get();
        Store savedStore = storeRepository.findById(store.getStoreId()).get();
        Assertions.assertEquals(likes.getStore(), savedStore);
        Assertions.assertEquals(likes.getMember().getEmail(), consumer.getEmail());

    }
    @Test
    @DisplayName("가게 좋아요를 취소했을 시 가게와 멤버에 대한 좋아요 entity는 삭제 되어있어야한다")
    public void whenDeleteStoreLike_ThenNoLikesEntityWithStoreIdMemberId() throws Exception {
        //given
        Store store = storeRepository.save(new Store("가게이름", "주소", "010-1234-1234"));
        Consumer consumer =
                consumerRepository.save(new Consumer(
                        "email@naber.cim", passwordEncoder.encode("asdad1234"),
                        "kimminji", AuthenticationType.CONSUMER, true
                ));
        likeRepository.save(new Likes(store, consumer));
        createAuthority(consumer);
        //when
        //then
        mockMvc.perform(
                        delete("/api/stores/{storeId}/likes", store.getStoreId()))
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Assertions.assertFalse(
                likeRepository
                        .findByStoreStoreIdAndMemberMemberId(store.getStoreId(), consumer.getMemberId()).isPresent());

    }

    @Test
    @DisplayName("가게 좋아요 추가하는 요청을 두번 보냈을땐 두번 저장되지 않고 throw exception")
    public void whenDuplicateAddStoreLike_ThenThrowException() throws Exception {
        //given
        Store store = storeRepository.save(new Store("가게이름", "주소", "010-1234-1234"));
        Consumer consumer =
                consumerRepository.save(new Consumer(
                        "email@naber.cim", passwordEncoder.encode("asdad1234"),
                        "kimminji", AuthenticationType.CONSUMER, true
                ));
        likeRepository.save(new Likes(store, consumer));
        createAuthority(consumer);

        em.flush();
        em.clear();
        //when
        //then
        mockMvc.perform(
                        post("/api/stores/{storeId}/likes", store.getStoreId()))
                .andExpect(status().isBadRequest());

        Likes likes = likeRepository.findByStoreStoreIdAndMemberMemberId(store.getStoreId(), consumer.getMemberId()).get();
        Store savedStore = storeRepository.findById(store.getStoreId()).get();
        Assertions.assertEquals(likes.getStore(), savedStore);
        Assertions.assertEquals(likes.getMember().getEmail(), consumer.getEmail());
        int s = 2;

    }
    @Test
    @DisplayName("인증되지 않은자가 가게 좋아요를 누르는 경우 throw exception")
    public void whenSetStoreLikeWithAnonymousUser_ThenThrowException() throws Exception {
        //given
        Store store = storeRepository.save(new Store("가게이름", "주소", "010-1234-1234"));
        Consumer consumer =
                consumerRepository.save(new Consumer(
                        "email@naber.cim", passwordEncoder.encode("asdad1234"),
                        "kimminji", AuthenticationType.CONSUMER, true
                ));
        likeRepository.save(new Likes(store, consumer));
        // createAuthority(consumer);
        //when
        //then
        mockMvc.perform(
                        delete("/api/stores/{storeId}/likes", store.getStoreId()))
                .andExpect(status().isForbidden());

    }


}
