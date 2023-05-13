package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.twogather.twogatherwebbackend.BizRegNumberValidatorStub;
import com.twogather.twogatherwebbackend.config.TestConfig;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.valid.BizRegNumberValidation;
import com.twogather.twogatherwebbackend.dto.valid.BizRegNumberValidator;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.time.LocalDate;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static org.hibernate.type.LocalTimeType.FORMATTER;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StoreOwnerController.class)
@ActiveProfiles("test")
public class StoreOwnerControllerTest extends ControllerTest{
    @MockBean
    private StoreOwnerService storeOwnerService;
    private static final String URL = "/api/owners/{memberId}";
    @MockBean
    private BizRegNumberValidator validator;
    @BeforeEach
    public void init(){
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MockitoAnnotations.openMocks(this);

    }
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
        when(storeOwnerService.getMemberWithAuthorities(anyLong())).thenReturn(STORE_OWNER_RESPONSE);
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
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("data.businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("data.businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("data.businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        )
                ));

    }


    @Test
    @DisplayName("가게주인정보업데이트")
    public void updateOwnerInfo_WhenUpdateOwnerInfo_ThenReturnOwnerInfo() throws Exception {
        //given
        when(storeOwnerService.update(any())).thenReturn(STORE_OWNER_RESPONSE);
        //when
        when(validator.isValid(any(StoreOwnerSaveUpdateRequest.class),any(ConstraintValidatorContext.class)))
                .thenReturn(true);
        //then

        mockMvc.perform(put(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(STORE_OWNER_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("owner/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("사업자의 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호").attributes(getPasswordFormat()),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("data.businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("data.businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("data.businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        )
                ));

    }

    @Test
    @DisplayName("가게주인등록")
    public void join_WhenOwnerSave_ThenReturnOwnerInfo() throws Exception {
        //given
        when(storeOwnerService.join(any())).thenReturn(STORE_OWNER_RESPONSE);
        //when
        //then

        mockMvc.perform(post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(STORE_OWNER_REQUEST))
                )
                .andExpect(status().isCreated())
                .andDo(document("owner/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호").attributes(getPasswordFormat()),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("data.businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("data.businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("data.businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        )
                ));

    }


}
