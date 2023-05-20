package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;

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

    private StoreOwner owner;
    private Store store;

    @BeforeEach
    public void setup(){
        owner = createOwner(storeOwnerRepository, passwordEncoder);
        store = createStore(storeRepository,owner);
        createAuthority(owner);
    }

    @Test
    public void whenUploadImage_ThenCreateImage() throws Exception {
        // Given

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

    private List<MultipartFile> createMockMultipartFiles() {
        String fieldName = "fileList";
        MockMultipartFile file1 = new MockMultipartFile(fieldName, "image1.jpg", "image/jpeg", "image data".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file2 = new MockMultipartFile(fieldName, "image2.jpg", "image/jpeg", "image data".getBytes(StandardCharsets.UTF_8));

        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file1);
        fileList.add(file2);

        return fileList;
    }
}
