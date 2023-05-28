package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateRequest;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class StoreExcludeGetAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StoreOwnerRepository ownerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager em;
    private StoreOwner owner;
    private Store store;
    private static final String URL = "/api/stores";

    @BeforeEach
    public void setup(){
        owner = createOwner(ownerRepository, passwordEncoder);
        createAuthority(owner);
    }

    //save, update 에 대해서 작성해놨음
    @Test
    @DisplayName("가게 저장 성공")
    public void whenSaveValidStore_ThenReturnStoreInfo() throws Exception {
        //given
        StoreSaveUpdateRequest storeRequest = new StoreSaveUpdateRequest(
                "가게이름", "전주시 임임동 23길1", "063-231-4444"
        );
        //when, then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.storeName").value(storeRequest.getStoreName()))
                .andExpect(jsonPath("$.data.address").value(storeRequest.getAddress()))
                .andExpect(jsonPath("$.data.phone").value(storeRequest.getPhone()))
                .andDo(MockMvcResultHandlers.print());

        Assertions.assertNotNull(storeRepository.findByName(storeRequest.getStoreName()));
    }


    @Test
    @DisplayName("가게 저장시 빈값, null, 올바르지않은 전화번호 형식에 대한 예외 throw")
    public void whenSaveIncludeNullFieldStore_ThenThrowsException() throws Exception {
        //given
        StoreSaveUpdateRequest storeRequest = new StoreSaveUpdateRequest(
                null, "", "01012312312"
        );
        //when, then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지않은 값을 입력하였습니다"))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유효한 값으로 가게 업데이트 성공 및 확인 test")
    public void whenUpdateValidStore_ThenReturnStoreInfo() throws Exception {
        //given
        Store store = createStore(storeRepository, owner);
        StoreSaveUpdateRequest storeRequest = new StoreSaveUpdateRequest(
                "새로운가게", "새로운 전주시 임임동 23길1", "063-232-4444"
        );
        //when, then
        mockMvc.perform(put(URL+"/{storeId}", store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.storeName").value(storeRequest.getStoreName()))
                .andExpect(jsonPath("$.data.address").value(storeRequest.getAddress()))
                .andExpect(jsonPath("$.data.phone").value(storeRequest.getPhone()))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유효하지않은 값(null field)으로 가게 업데이트 시도 시 실패하면서 롤백되는 test")
    public void whenUpdateStoreIncludeNullField_ThenThrowException() throws Exception {
        //given
        Store store = createStore(storeRepository, owner);
        StoreSaveUpdateRequest storeRequest = new StoreSaveUpdateRequest(
                null, "새로운 전주시 임임동 23길1", "063-232-4441"
        );
        //when,then
        mockMvc.perform(put(URL+"/{storeId}", store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지않은 값을 입력하였습니다"))
                .andDo(MockMvcResultHandlers.print());

        Store savedStore = storeRepository.findById(store.getStoreId()).get();
        Assertions.assertNotEquals(savedStore.getAddress(), storeRequest.getAddress());
        Assertions.assertNotEquals(savedStore.getPhone(), storeRequest.getPhone());
    }

    @Test
    @DisplayName("유효하지않은 전화번호로 가게 업데이트 시도 시 실패하면서 롤백되는 test")
    public void whenUpdateStoreIncludePhoneField_ThenThrowException() throws Exception {
        //given
        Store store = createStore(storeRepository, owner);
        StoreSaveUpdateRequest storeRequest = new StoreSaveUpdateRequest(
                "storename1", "새로운 전주시 임임동 23길1", "063232-4441"
        );
        //when,then
        mockMvc.perform(put(URL+"/{storeId}", store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지않은 값을 입력하였습니다"))
                .andDo(MockMvcResultHandlers.print());

        Store savedStore = storeRepository.findById(store.getStoreId()).get();
        Assertions.assertNotEquals(savedStore.getAddress(), storeRequest.getAddress());
        Assertions.assertNotEquals(savedStore.getPhone(), storeRequest.getPhone());
    }


    @Test
    @DisplayName("가게 삭제 요청 후 진짜 삭제되었는지 확인해보는 test")
    public void whenDeleteStore_ThenNotExistStore() throws Exception {
        //given
        Store store = createStore(storeRepository, owner);

        //when,then
        mockMvc.perform(delete(URL+"/{storeId}", store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Assertions.assertFalse(storeRepository.findById(store.getStoreId()).isPresent());
    }
    @Test
    @DisplayName("오직 자신의 가게만 삭제할 수 있다")
    //근데 이거 남의 가게인 경우.. 만 forbidden이 떠야하는건지 상관없이 자기 소유 가게가 아니면 forbidden throw해도되는건지..
    public void whenDeleteNotMyStore_ThenThrowException() throws Exception {
        //given
        Long noSuchId = 131232l;
        //when,then
        mockMvc.perform(delete(URL+"/{storeId}", noSuchId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(MockMvcResultHandlers.print());

        Assertions.assertFalse(storeRepository.findById(noSuchId).isPresent());
    }
}
