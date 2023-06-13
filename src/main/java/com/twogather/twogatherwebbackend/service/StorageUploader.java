package com.twogather.twogatherwebbackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface StorageUploader {
    String upload(String directory, MultipartFile image);
    String upload(String directory, File image);
    List<String> uploadList(String directory, List<MultipartFile> list);
    List<String> uploadList(List<MultipartFile> list);
    void delete(String imageUrl);
    boolean doesObjectExist(String url);
}
