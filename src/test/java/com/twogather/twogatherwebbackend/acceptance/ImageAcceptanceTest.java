package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Image;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.menu.MenuIdList;
import com.twogather.twogatherwebbackend.repository.ImageRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ImageAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StoreOwnerRepository storeOwnerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ImageRepository imageRepository;

    private StoreOwner owner;
    private Store store;

    /*
    @BeforeEach
    public void setup(){
        owner = createOwner(storeOwnerRepository, passwordEncoder);
        store = createStore(storeRepository,owner);
    }

    @Test
    public void whenUploadImage_ThenCreateImage() throws Exception {
        // Given
        createAuthority(owner);
        List<MultipartFile> fileList = createMockMultipartFiles();
        // When
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders
                        .multipart("/api/stores/" + store.getStoreId() + "/images")
                        .file((MockMultipartFile) fileList.get(0)) // Add the first file to the request
                        .file((MockMultipartFile) fileList.get(1)); // Add the second file to the request

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2));

    }

    @Test
    @DisplayName("탈퇴한 회원으로 이미지 업로드시 권한 exception")
    public void whenUploadImageWithLeaveMember_ThenThrowException() throws Exception {
        // Given
        StoreOwner leavedOwner = storeOwnerRepository.save( new StoreOwner("owner", "owner@naver.com",passwordEncoder.encode("adasdsad123"), "김사업", AuthenticationType.STORE_OWNER, false));
        Store storeByLeavedMember = createStore(storeRepository,leavedOwner);
        List<MultipartFile> fileList = createMockMultipartFiles();
        createAuthority(leavedOwner);
        // When
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders
                        .multipart("/api/stores/" + storeByLeavedMember.getStoreId() + "/images")
                        .file((MockMultipartFile) fileList.get(0))
                        .file((MockMultipartFile) fileList.get(1));

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증된 사용자가 아닙니다"))
                .andDo(MockMvcResultHandlers.print());

    }
    @Test
    public void whenDeleteImage_ThenCreateImage() throws Exception {
        // Given
        createAuthority(owner);
        Image image1 = imageRepository.save(new Image(store, "url1"));
        Image image2 = imageRepository.save(new Image(store, "url2"));
        List<Long> idList = new ArrayList<>(){{
            add(image1.getImageId());
            add(image2.getImageId());
        }};
        MenuIdList request = new MenuIdList(idList);
        // When
        mockMvc.perform(delete("/api/stores/" + store.getStoreId() + "/images", store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        boolean image1IsExist = imageRepository.findById(image1.getImageId()).isPresent();
        boolean image2IsExist = imageRepository.findById(image2.getImageId()).isPresent();
        Assertions.assertTrue(image1IsExist);
        Assertions.assertTrue(image2IsExist);
    }

    @Test
    @DisplayName("탈퇴한 회원으로 이미지 삭제시 throw exception")
    public void whenDeleteImageWithLeaveMember_ThenThrowException() throws Exception {
        // Given
        StoreOwner leavedOwner = storeOwnerRepository.save( new StoreOwner("owner", "owner@naver.com",passwordEncoder.encode("adasdsad123"), "김사업", AuthenticationType.STORE_OWNER, false));
        Store storeByLeavedMember = createStore(storeRepository,leavedOwner);
        createAuthority(leavedOwner);
        Image image1 = imageRepository.save(new Image(store, "url1"));
        Image image2 = imageRepository.save(new Image(store, "url2"));
        List<Long> idList = new ArrayList<>(){{
            add(image1.getImageId());
            add(image2.getImageId());
        }};
        MenuIdList request = new MenuIdList(idList);
        // When
        mockMvc.perform(delete("/api/stores/{storeId}/images", storeByLeavedMember.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증된 사용자가 아닙니다"))
                .andDo(MockMvcResultHandlers.print());

        boolean image1IsExist = imageRepository.findById(image1.getImageId()).isPresent();
        boolean image2IsExist = imageRepository.findById(image2.getImageId()).isPresent();
        Assertions.assertTrue(image1IsExist);
        Assertions.assertTrue(image2IsExist);
    }

    @Test
    public void whenDeleteNoSuchImage_ThenNotThrowException() throws Exception {
        // Given
        createAuthority(owner);
        Image image1 = imageRepository.save(new Image(store, "url1"));
        Long noSuchImageId = 12312312l;
        List<Long> idList = new ArrayList<>(){{
            add(image1.getImageId());
            add(noSuchImageId);
        }};
        MenuIdList request = new MenuIdList(idList);
        // When
        mockMvc.perform(delete("/api/stores/" + store.getStoreId() + "/images", store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        boolean image1IsExist = imageRepository.findById(image1.getImageId()).isPresent();
        Assertions.assertTrue(image1IsExist);
    }

    private List<MultipartFile> createMockMultipartFiles() {
        String fieldName = "fileList";
        MockMultipartFile file1 = new MockMultipartFile(fieldName, "image1.jpg", "image/jpeg", "image data".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file2 = new MockMultipartFile(fieldName, "image2.jpg", "image/jpeg", "image data".getBytes(StandardCharsets.UTF_8));

        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file1);
        fileList.add(file2);

        return fileList;
    }*/
}
