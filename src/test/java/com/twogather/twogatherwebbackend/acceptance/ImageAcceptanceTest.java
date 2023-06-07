package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.repository.ImageRepository;
import com.twogather.twogatherwebbackend.service.S3Uploader;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.TestUtil.convert;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class ImageAcceptanceTest extends AcceptanceTest{
    @Autowired
    private S3Uploader s3Uploader;
    @Autowired
    private ImageRepository imageRepository;

    @BeforeEach
    public void initSetting(){
        super.setUp();
        registerOwner();
        registerStore();
        approveStore();
        url = "/api/stores/" + storeId + "/images";
    }

    private Long imageId1;
    private Long imageId2;
    private String imageUrl1;
    private String imageUrl2;
    private String url;

    public void createImages(){
        List<File> fileList = createMockFiles();
        List<ImageResponse> responseList =
                convert(doPostWithFile(fileList)
                        .extract()
                        .as(Response.class), new TypeReference<List<ImageResponse>>() {});
        imageId1 = responseList.get(0).getImageId();
        imageId2 = responseList.get(1).getImageId();
        imageUrl1 = responseList.get(0).getUrl();
        imageUrl2 = responseList.get(1).getUrl();
    }
    @Test
    public void whenUploadImage_ThenCreateImage() throws Exception {
        // Given,then
        createImages();

        Assertions.assertTrue(s3Uploader.doesObjectExist(imageUrl1));
        Assertions.assertTrue(s3Uploader.doesObjectExist(imageUrl2));
    }


    @Test
    @DisplayName("탈퇴한 회원으로 이미지 업로드시 권한 exception")
    public void whenUploadImageWithLeaveMember_ThenThrowException() {
        // Given
        List<File> fileList = createMockFiles();

        leaveOwner();

        //then
        doPostWithFile(fileList)
                .statusCode(HttpStatus.UNAUTHORIZED.value());

    }
    @Test
    public void whenDeleteImage_ThenCreateImage() {
        // Given
        createImages();
        ImageIdList request = createImageIdRequest();

        // When
        doDeleteWithFile(request)
                .statusCode(HttpStatus.OK.value());
        //then
        Assertions.assertFalse(imageRepository.findById(imageId1).isPresent());
        Assertions.assertFalse(imageRepository.findById(imageId2).isPresent());
        Assertions.assertFalse(s3Uploader.doesObjectExist(imageUrl1));
        Assertions.assertFalse(s3Uploader.doesObjectExist(imageUrl2));
    }

    @Test
    @DisplayName("탈퇴한 회원으로 이미지 삭제시 throw exception")
    public void whenDeleteImageWithLeaveMember_ThenThrowException() throws Exception {
        // Given
        createImages();
        leaveOwner();
        ImageIdList request = createImageIdRequest();

        // When
        doDeleteWithFile(request)
                .statusCode(HttpStatus.UNAUTHORIZED.value());

        Assertions.assertTrue(imageRepository.findById(imageId1).isPresent());
        Assertions.assertTrue(imageRepository.findById(imageId2).isPresent());
        Assertions.assertTrue(s3Uploader.doesObjectExist(imageUrl1));
        Assertions.assertTrue(s3Uploader.doesObjectExist(imageUrl2));

    }

    @Test
    public void whenDeleteNoSuchImage_ThenNotThrowException() {
        // Given
        createImages();
        ImageIdList request = createNoSuchImageIdRequest();
        // When
        doDeleteWithFile(request)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    private List<File> createMockFiles() {
        List<File> fileList = new ArrayList<>();
        try {
            File tempFile1 = File.createTempFile("tempFile1", ".txt");
            FileWriter writer1 = new FileWriter(tempFile1);
            writer1.write("This is a temporary file content for tempFile1");
            writer1.close();
            fileList.add(tempFile1);

            File tempFile2 = File.createTempFile("tempFile2", ".txt");
            FileWriter writer2 = new FileWriter(tempFile2);
            writer2.write("This is a temporary file content for tempFile2");
            writer2.close();
            fileList.add(tempFile2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }
    private ImageIdList createImageIdRequest(){
        List<Long> idList = new ArrayList<>(){{
            add(imageId1);
            add(imageId2);
        }};
        return new ImageIdList(idList);
    }
    private ImageIdList createNoSuchImageIdRequest(){
        Long noSuchImageId = 12312312l;
        List<Long> idList = new ArrayList<>(){{
            add(noSuchImageId);
        }};
        return new ImageIdList(idList);
    }

    private <T> ValidatableResponse doDeleteWithFile(T request) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getRefreshToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getAccessToken())
                .body(request)
                .when()
                .delete(url)
                .then()
                .log().all();
    }
    private <T> ValidatableResponse doPostWithFile(List<File> fileList) {
        return given()
                .multiPart("storeImageList", fileList.get(0))
                .multiPart("storeImageList", fileList.get(1))
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getRefreshToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getAccessToken())
                .when()
                .post(url)
                .then()
                .log().all();
    }

}
