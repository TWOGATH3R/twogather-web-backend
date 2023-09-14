package com.twogather.twogatherwebbackend.service;


import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.twogather.twogatherwebbackend.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.exception.FileException.FileErrorCode.FILE_CONVERTER_ERROR;

@RequiredArgsConstructor
@Slf4j
@Component
public class S3Uploader implements StorageUploader {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.s3.store.directory}")
    private String directory;
    @Value("${aws.s3.region}")
    private String region;
    private final AmazonS3 amazonS3Client;

    @Override
    public boolean doesObjectExist(String objectUrl) {
        try {
            String key = extractKeyFromUrl(objectUrl);

            return amazonS3Client.doesObjectExist(bucketName, key);
        } catch (RuntimeException e) {
            throw new AmazonClientException("Amazon client exception", e);
        }
    }
    @Override
    public String upload(String directory, File uploadFile) {
        String fileName = UUID.randomUUID() + "";
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        log.info("File Upload : " + fileName);

        removeNewFile(uploadFile);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }
    @Override
    public void deleteList(List<String> urlList) {
        urlList.forEach(this::delete);
    }

    @Override
    public List<String> uploadList(String directory, List<MultipartFile> list) {
        return list.stream()
                .map(file -> upload(directory, file))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> uploadList(List<MultipartFile> list) {
        return list.stream()
                .map(file -> upload(directory, file))
                .collect(Collectors.toList());
    }


    @Override
    public String upload(String directory, MultipartFile multipartFile){
        File uploadFile = null;
        try {
            uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new FileException(FILE_CONVERTER_ERROR));
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileException(FILE_CONVERTER_ERROR);
        }
        return upload(directory, uploadFile);
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());

        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
        }
        return Optional.of(convertFile);

    }

    @Override
    public void delete(String objectUrl) {
        try {
            String key = extractKeyFromUrl(objectUrl);

            if (amazonS3Client.doesObjectExist(bucketName, key)) {
                amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
            } else {
                log.warn("해당 이미지의 파일이 없습니다. url : {}", objectUrl);
            }
        } catch (RuntimeException e) {
            throw new AmazonClientException("amazon client exception", e);
        }
        log.info("image deleted: {}", objectUrl);
    }

    private String extractKeyFromUrl(String objectUrl) {
        return objectUrl.replace("https://" + bucketName + ".s3." + region + ".amazonaws.com/", "");
    }
    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        }else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }
}
