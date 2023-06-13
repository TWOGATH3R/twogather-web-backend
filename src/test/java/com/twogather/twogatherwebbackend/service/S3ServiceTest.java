package com.twogather.twogatherwebbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class S3ServiceTest {

    /*
    @Test
    public void testUploadAndDelete() throws IOException {
        // 업로드할 파일 생성
        File file = new File("file.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", "file".getBytes(StandardCharsets.UTF_8));

        // 파일 업로드
        String directory = "uploads";
        String imageUrl = s3Uploader.upload(directory, multipartFile);
        System.out.println("Uploaded image URL: " + imageUrl);

        // 파일 삭제
        s3Uploader.delete(imageUrl);
    }*/
}
