package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreKeyword;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.repository.KeywordRepository;
import com.twogather.twogatherwebbackend.repository.StoreKeywordRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class KeywordAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StoreOwnerRepository ownerRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private StoreKeywordRepository storeKeywordRepository;
    @Autowired
    private EntityManager em;
    String URL = "/api/keywords";
    @Test
    @DisplayName("다섯개보다 많은 키워드가 있을때 다섯개의 키워드만 랜덤으로 조회해온다")
    public void whenExistKeywordMoreThanFive_ThenRandomKeywordList() throws Exception {
        keywordRepository.save(new Keyword("keyword1"));
        keywordRepository.save(new Keyword("keyword2"));
        keywordRepository.save(new Keyword("keyword3"));
        keywordRepository.save(new Keyword("keyword4"));
        keywordRepository.save(new Keyword("keyword5"));
        keywordRepository.save(new Keyword("keyword6"));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(5))
                .andExpect(jsonPath("$.data[*].name").exists())
                .andExpect(jsonPath("$.data[*].keywordId").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("다섯개보다 적은 키워드가 있을때 해당 개수의 키워드만 랜덤으로 조회해온다")
    public void whenExistKeywordLessThanFive_ThenRandomKeywordList() throws Exception {
        keywordRepository.save(new Keyword("keyword1"));
        keywordRepository.save(new Keyword("keyword2"));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[*].name").exists())
                .andExpect(jsonPath("$.data[*].keywordId").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("가게의 키워드 세개 설정")
    public void whenSettingThreeKeywordsForStore_thenTheyAreSavedInStoreKeywordTable() throws Exception {
        //given
        StoreOwner owner = createOwner(ownerRepository, passwordEncoder);
        Store store = createStore(storeRepository,owner);
        //createAuthority(owner);

        List<String> keywordList = new ArrayList<>(){{
            add("저렴한");
            add("분위기 좋은");
            add("아이들과 오기 좋은");
        }};
        mockMvc.perform(post(URL + "/stores/" + store.getStoreId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(keywordList)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        em.flush();
        em.clear();
        List<StoreKeyword> storeKeywordList = storeKeywordRepository.findByStoreStoreId(store.getStoreId());

        assertTrue(storeKeywordList.stream().anyMatch(sk -> sk.getKeyword().getName().equals("저렴한")));
        assertTrue(storeKeywordList.stream().anyMatch(sk -> sk.getKeyword().getName().equals("분위기 좋은")));
        assertTrue(storeKeywordList.stream().anyMatch(sk -> sk.getKeyword().getName().equals("아이들과 오기 좋은")));
    }

    @Test
    @DisplayName("가게의 키워드 세개이상으로 입력이 들어온경우 throw exception")
    public void whenSettingTwoKeywordsForStore_thenThrowException() throws Exception {
        //given
        StoreOwner owner = createOwner(ownerRepository, passwordEncoder);
        Store store = createStore(storeRepository,owner);
        //createAuthority(owner);

        List<String> keywordList = new ArrayList<>(){{
            add("저렴한");
            add("분위기 좋은");
            add("분위기 좋은2");
            add("분위기 좋은3");
        }};
        mockMvc.perform(post(URL + "/stores/" + store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(keywordList)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("정해진 개수만큼의 키워드만 입력할 수 있습니다"))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("가게의 키워드가 빈 값이 들어온 경우 무시한다")
    public void whenEmptyKeyword_thenIgnore() throws Exception {
        //given
        StoreOwner owner = createOwner(ownerRepository, passwordEncoder);
        Store store = createStore(storeRepository,owner);
        createAuthority(owner);

        List<String> keywordList = new ArrayList<>(){{
            add("");
            add("분위기 좋은");
            add("아이들과 오기 좋은");
        }};
        mockMvc.perform(post(URL + "/stores/" + store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(keywordList)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        em.flush();
        em.clear();
        List<StoreKeyword> storeKeywordList = storeKeywordRepository.findByStoreStoreId(store.getStoreId());

        assertTrue(storeKeywordList.size()==2);
        assertTrue(storeKeywordList.stream().anyMatch(sk -> sk.getKeyword().getName().equals("분위기 좋은")));
        assertTrue(storeKeywordList.stream().anyMatch(sk -> sk.getKeyword().getName().equals("아이들과 오기 좋은")));
    }
}
