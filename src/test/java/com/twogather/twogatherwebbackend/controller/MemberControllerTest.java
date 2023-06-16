package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
import com.twogather.twogatherwebbackend.dto.member.FindUsernameRequest;
import com.twogather.twogatherwebbackend.dto.member.PasswordRequest;
import com.twogather.twogatherwebbackend.service.MemberService;
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

import javax.sound.sampled.Control;

import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getPasswordFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MemberController.class)
public class MemberControllerTest extends ControllerTest {
    @MockBean
    private MemberService memberService;

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
                                        .writeValueAsString(new FindUsernameRequest("fias@naver.com", "홍길동"))))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("member/get-username",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명")
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
}
