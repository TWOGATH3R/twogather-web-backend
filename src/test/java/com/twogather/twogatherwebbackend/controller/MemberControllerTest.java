package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
import com.twogather.twogatherwebbackend.dto.member.FindUsernameRequest;
import com.twogather.twogatherwebbackend.dto.member.PasswordRequest;
import com.twogather.twogatherwebbackend.dto.store.MyLikeStoreResponse;
import com.twogather.twogatherwebbackend.service.MemberService;
import com.twogather.twogatherwebbackend.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.sound.sampled.Control;

import java.util.List;

import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MemberController.class)
public class MemberControllerTest extends ControllerTest {
    @MockBean
    private MemberService memberService;
    @MockBean
    private StoreService storeService;


    @Test
    @DisplayName("비밀번호 검증")
    public void WhenVerifyPassword_ThenReturnTrueOrFalse() throws Exception {
        //given
        when(memberService.verifyPassword(any())).thenReturn(true);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/{memberId}/verify-password",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .writeValueAsString(new PasswordRequest("passsword1"))))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("member/verify-password",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("고객의 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("기존 비밀번호").attributes(getPasswordFormat())
                        ),
                        responseFields(
                                fieldWithPath("data.isValid").type(JsonFieldType.BOOLEAN).description("비밀번호 일치 여부")
                        )
                ));

    }

    @Test
    @DisplayName("비밀번호 변경")
    public void WhenChangePassword_ThenSuccess() throws Exception {
        //given
        doNothing().when(memberService).changePassword(any());
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/members/{memberId}/password",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .writeValueAsString(new PasswordRequest("passsword1"))))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("member/change-password",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("고객의 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("바꿀 비밀번호").attributes(getPasswordFormat())
                        )
                ));

    }

    @Test
    @DisplayName("아이디 조회")
    public void WhenFindUsername_ThenSuccess() throws Exception {
        //given
        when(memberService.findMyUsername(any())).thenReturn("us***");
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/my-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .writeValueAsString(new FindUsernameRequest("fias@naver.com", "김뿌웅치"))))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("member/get-username",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명(닉네임)").attributes(getMemberNameFormat())
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.STRING).description("아이디")
                        )
                ));

    }

    @Test
    @DisplayName("이메일 조회")
    public void WhenExistEmail_ThenTrue() throws Exception {
        //given
        when(memberService.isExist(any())).thenReturn(true);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/checks-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .writeValueAsString(new EmailRequest("fias@naver.com"))))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("member/checks-email",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("존재하는지 확인할 이메일")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("해당 이메일로 가입된 사용자의 유무")
                        )
                ));

    }

    @Test
    @DisplayName("좋아요 누른 가게 조회")
    public void WhenFindLikeStore_ThenSuccess() throws Exception {
        //given
        when(storeService.findMyLikeStore(anyLong(), any())).thenReturn(createMyLikeResponse());
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/{memberId}/likes/?page=0&size=10", 1))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("member/find-like-store",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("고객의 고유 id")
                        ),
                        requestParameters(
                                parameterWithName("page").description("조회할 페이지의 수").optional(),
                                parameterWithName("size").description("검사결과 최대 개수").optional()
                        ),
                        responseFields(
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 고유 ID"),
                                fieldWithPath("data[].storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("가게 전화번호"),
                                fieldWithPath("data[].storeImageUrl").type(JsonFieldType.STRING).description("가게사진"),
                                fieldWithPath("data[].keywordList").type(JsonFieldType.ARRAY).description("가게 키워드 리스트"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 페이지 수"),
                                fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("현재 페이지의 아이템 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 개수"),
                                fieldWithPath("isLast").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                fieldWithPath("isFirst").type(JsonFieldType.BOOLEAN).description("첫 페이지인지 여부"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재페이지가 몇번인지")

                        )
                ));

    }

    private Page<MyLikeStoreResponse> createMyLikeResponse(){
        List<MyLikeStoreResponse> responses = List.of(
                MyLikeStoreResponse.builder()
                        .storeId(1L)
                        .storeName("Store 1")
                        .address("Address 1")
                        .phone("Phone 1")
                        .storeImageUrl("Image URL 1")
                        .keywordList(List.of("맛있는", "분위기 있는", "감성 있는"))
                        .build(),
                MyLikeStoreResponse.builder()
                        .storeId(2L)
                        .storeName("Store 2")
                        .address("Address 2")
                        .phone("Phone 2")
                        .storeImageUrl("Image URL 2")
                        .keywordList(List.of("아이들과 오기 좋은", "청결한", "감성 있는"))
                        .build(),
                MyLikeStoreResponse.builder()
                        .storeId(3L)
                        .storeName("Store 3")
                        .address("Address 3")
                        .phone("Phone 3")
                        .storeImageUrl("Image URL 3")
                        .keywordList(List.of("아이들과 오기 좋은", "조용한"))
                        .build()
        );
        return new PageImpl<>(responses);

    }
}
