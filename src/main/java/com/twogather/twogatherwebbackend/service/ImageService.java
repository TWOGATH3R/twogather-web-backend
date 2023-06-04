package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Image;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.exception.ImageException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.ImageRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.ImageException.ImageErrorCode.NO_SUCH_IMAGE;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final StoreRepository storeRepository;
    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;
    private static final String DIRECTORY_NAME = "store";

    public List<ImageResponse> upload(Long storeId, List<MultipartFile> fileList){
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreException(NO_SUCH_STORE)
        );
        List<String> uploadedFileUrlList = s3Uploader.uploadList(DIRECTORY_NAME, fileList);
        List<Image> imageList = toImageEntityList(uploadedFileUrlList, store);
        List<Image> savedImageList = imageRepository.saveAll(imageList);
        List<ImageResponse> responseList = toImageResponseList(savedImageList);
        return responseList;
    }
    public void delete(ImageIdList idList){
        for (Long id :idList.getImageIdList()){
            Image image = imageRepository.findById(id).orElseThrow(
                    ()->new ImageException(NO_SUCH_IMAGE)
            );
            imageRepository.delete(image);
            s3Uploader.delete(image.getUrl());
        }

    }
    public List<ImageResponse> getStoreImageInfos(Long storeId){
        //TODO:구현
        return new ArrayList<>();
    }

    private List<ImageResponse> toImageResponseList(List<Image> imageList){
        List<ImageResponse> responseList = new ArrayList<>();
        for (Image image: imageList){
            responseList.add(new ImageResponse(image.getImageId(), image.getUrl()));
        }
        return responseList;
    }
    private List<Image> toImageEntityList(List<String> urlList, Store store){
        List<Image> imageList = new ArrayList<>();
        for (String url: urlList){
            imageList.add(new Image(store, url));
        }
        return imageList;
    }
}
