package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveRequest;
import com.twogather.twogatherwebbackend.dto.member.MemberUpdateRequest;
import com.twogather.twogatherwebbackend.dto.member.PasswordRequest;
import com.twogather.twogatherwebbackend.service.MemberService;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreOwnerController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class StoreOwnerControllerTest extends ControllerTest{
    @MockBean
    private StoreOwnerService storeOwnerService;
    @MockBean
    private MemberService memberService;
    private static final String URL = "/api/owners/{memberId}";
    @Test
    @DisplayName("가게주인탈퇴")
    public void leave_WhenOwnerLeave_Then200Ok() throws Exception {
        //given
        doNothing().when(storeOwnerService).delete(anyLong());
        //when
        //then
        mockMvc.perform(delete(URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("owner/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("사업자의 고유 id")
                        )
                ));

    }

    @Test
    @DisplayName("가게주인정보조회")
    public void getOwnerInfo_WhenGetOwnerInfo_ThenReturnOwnerInfo() throws Exception {
        //given
        when(storeOwnerService.getOwnerInfo(anyLong())).thenReturn(
                new MemberResponse(1l, "nick1", "dda@naver.com",
                        "가게주인이름"));
        //when
        //then

        mockMvc.perform(get(URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("owner/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("사업자의 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명").attributes(getMemberNameFormat())
                      )
                ));

    }


    @Test
    @DisplayName("가게주인정보업데이트")
    public void updateOwnerInfo_WhenUpdateOwnerInfo_ThenReturnOwnerInfo() throws Exception {
        //given
        when(memberService.update(anyLong(), any())).thenReturn(
                new MemberResponse(1l, "nick1", "dda@naver.com",
                "가게주인이름"));
      //then
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(new MemberUpdateRequest(
                                                "ad@naer.com", "name1", "홍길동"
                                        )))
                )
                .andExpect(status().isOk())
                .andDo(document("owner/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("사업자의 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일").optional(),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()).optional(),
                               fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명(닉네임)").optional().attributes(getMemberNameFormat())

                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명").attributes(getMemberNameFormat())

                        )
                ));

    }

    @Test
    @DisplayName("가게주인등록")
    public void join_WhenOwnerSave_ThenReturnOwnerInfo() throws Exception {
        //given
        when(storeOwnerService.join(any())).thenReturn(new MemberResponse(1l, "nick1", "dda@naver.com",
                "가게주인이름"));
        //when
        //then

        mockMvc.perform(post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(new MemberSaveRequest(
                                                "ad@naer.com", "name1","Asdawd213", "홍길동"
                                        )))
                )
                .andExpect(status().isCreated())
                .andDo(document("owner/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호").attributes(getPasswordFormat()),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명").attributes(getMemberNameFormat())

                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat())

                        )
                ));

    }

}
