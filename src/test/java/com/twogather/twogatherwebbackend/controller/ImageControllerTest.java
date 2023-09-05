package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.util.TestConstants;
import com.twogather.twogatherwebbackend.service.ImageService;
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

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ImageController.class)
public class ImageControllerTest extends ControllerTest{
    @MockBean
    private ImageService imageService;

    private static final String URL = "/api/stores/{storeId}/images";

    /*
    @Test
    @DisplayName("이미지 업로드")
    public void uploadWhenImageSave_Then201() throws Exception {
        //given
        when(imageService.upload(anyLong(), any())).thenReturn(IMAGE_RESPONSE_LIST);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL,1)
                        .file(IMAGE_REQUEST_PART2)
                        .file(TestConstants.IMAGE_REQUEST_PART2)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .characterEncoding("UTF-8")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andDo(document("image/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("해당 storeId에 대해 모든 가게 이미지를 조회해오기 위해 필요한 정보")
                        ),
                        responseFields(
                                fieldWithPath("data[].url").type(JsonFieldType.STRING).description("이미지 링크에 해당하는 url"),
                                fieldWithPath("data[].imageId").type(JsonFieldType.NUMBER).description("이미지 고유 id")
                       )
                ));

    }
*/
    @Test
    @DisplayName("가게 이미지 리스트 삭제")
    public void deleteStoreImage_WhenDeleteStoreImage_Then200Ok() throws Exception {
        //given
        doNothing().when(imageService).delete(any());
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(IMAGE_ID_LIST))
                )
                .andExpect(status().isOk())
                .andDo(document("image/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("해당 storeId에 대해 모든 가게 이미지를 조회해오기 위해 필요한 정보")
                        ),
                        requestFields(
                                fieldWithPath("imageIdList").type(JsonFieldType.ARRAY).description("삭제대상인 이미지 고유 id 리스트")
                        )
                ));

    }

    @Test
    @DisplayName("가게 이미지 리스트 조회")
    public void getStoreImageInfos_WhenGetStoreImageInfos_ThenReturnFileUrl() throws Exception {
        //given
        when(imageService.getStoreImageInfos(any())).thenReturn(IMAGE_RESPONSE_LIST);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.get(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("image/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("해당 storeId에 대해 모든 가게 이미지를 조회해오기 위해 필요한 정보")
                        ),
                        responseFields(
                                fieldWithPath("data[].url").type(JsonFieldType.STRING).description("이미지 링크에 해당하는 url"),
                                fieldWithPath("data[].imageId").type(JsonFieldType.NUMBER).description("이미지 고유 id")
                        )
                ));

    }
}
