package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    public List<ImageResponse> upload(List<MultipartFile> fileList){
        return new ArrayList<>();
    }
    public void delete(ImageIdList idList){

    }
    public List<ImageResponse> getStoreImageInfos(Long storeId){
        return new ArrayList<>();
    }
}
