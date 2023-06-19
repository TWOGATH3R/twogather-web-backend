package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.exception.ImageException;
import com.twogather.twogatherwebbackend.repository.ImageRepository;
import com.twogather.twogatherwebbackend.service.S3Uploader;
import com.twogather.twogatherwebbackend.service.StorageUploader;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.ImageException.ImageErrorCode.NOT_IMAGE;
import static com.twogather.twogatherwebbackend.exception.ImageException.ImageErrorCode.NO_SUCH_IMAGE;
import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class ImageAcceptanceTest extends AcceptanceTest{
    @Autowired
    private StorageUploader s3Uploader;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private  ResourceLoader loader;
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
    /*
    @Test
    public void whenUploadImage_ThenCreateImageWithProd() throws Exception {
        // Given,then
        createImages();

        Assertions.assertTrue(s3Uploader.doesObjectExist(imageUrl1));
        Assertions.assertTrue(s3Uploader.doesObjectExist(imageUrl2));
    }*/


    @Test
    @DisplayName("특정 가게의 모든 이미지를 권한없이 얻어올수있다")
    public void whenFindStoreImage_ThenSuccess() {
        // Given
        String url = "/api/stores/" + storeId + "/images";
        //when
        createImages();
        //then
        doGet(url,null,null)
                .statusCode(HttpStatus.OK.value())
                .body("data.imageId", notNullValue())
                .body("data.url", hasItem(imageUrl1))
                .body("data.url", hasItem(imageUrl2));

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
    @DisplayName("이미지가 아닌 사진을 올릴 경우 throw exception")
    public void whenUploadNotImage_ThenThrowException() {
        // Given
        List<File> fileList = createNonImageFile();

        //then
        doPostWithFile(fileList)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(NOT_IMAGE.getMessage()));

    }

    @Test
    @DisplayName("이미지를 삭제하는 요청을 보냈을떄, 스토리지와 데이터베이스 모두에서 삭제되어야한다")
    public void whenDeleteImage_ThenSuccessDelete() {
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

    /*
    @Test
    @DisplayName("탈퇴한 회원으로 이미지 삭제시 throw exception")
    public void whenDeleteImageWithLeaveMember_ThenThrowExceptionWithProd() throws Exception {
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

    }*/

    @Test
    @DisplayName("존재하지않는 id로 이미지를 삭제했을때 4xx error 가 터져야한다")
    public void whenDeleteNoSuchImage_ThenNotThrowException() {
        // Given
        createImages();
        ImageIdList request = createNoSuchImageIdRequest();
        // When
        doDeleteWithFile(request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(NO_SUCH_IMAGE.getMessage()));
    }
    private List<File> createMockFiles() {
        List<File> fileList = new ArrayList<>();

        File multipartFile = new File(".\\src\\test\\resources\\files\\image.jpg");


        fileList.add(multipartFile);
        fileList.add(multipartFile);

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
        ValidatableResponse response = null;
        response  = given()
                .multiPart("storeImageList", fileList.get(0))
                .multiPart("storeImageList", fileList.get(1))
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getRefreshToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getAccessToken())
                .when()
                .post(url)
                .then()
                .log().all();
        return response;
    }
    private List<File> createNonImageFile(){
        List<File> fileList = new ArrayList<>();

        File multipartFile = new File(".\\src\\test\\resources\\files\\text.txt");

        fileList.add(multipartFile);
        fileList.add(multipartFile);

        return fileList;
    }

}
