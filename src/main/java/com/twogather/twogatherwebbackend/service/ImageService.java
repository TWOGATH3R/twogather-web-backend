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
        //TODO:구현
        return new ArrayList<>();
    }
    public void delete(ImageIdList idList){
        //TODO:구현

    }
    public List<ImageResponse> getStoreImageInfos(Long storeId){
        //TODO:구현
        return new ArrayList<>();
    }
}
