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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.exception.ImageException.ImageErrorCode.*;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final StoreRepository storeRepository;
    private final ImageRepository imageRepository;
    private final StorageUploader s3Uploader;

    @Value("${image.max.size}")
    private Integer ImageMaxSize;

    public List<ImageResponse> upload(Long storeId, List<MultipartFile> fileList){
        isImageFiles(fileList);

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreException(NO_SUCH_STORE)
        );

        validateMaxSize(store);

        List<String> uploadedFileUrlList = s3Uploader.uploadList(fileList);
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
        List<Image> imageList = imageRepository.findByStoreStoreId(storeId);

        return imageList.stream()
                .map(image -> new ImageResponse(image.getImageId(), image.getUrl()))
                .collect(Collectors.toList());
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
    private boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();

        if (StringUtils.hasText(contentType) && contentType.startsWith("image/")) {
            return true;
        }

        String fileName = file.getOriginalFilename();
        if (StringUtils.hasText(fileName)) {
            String fileExtension = StringUtils.getFilenameExtension(fileName);
            if (StringUtils.hasText(fileExtension) && fileExtension.matches("(?i)^(jpg|jpeg|png|gif|bmp)$")) {
                return true;
            }
        }

        return false;
    }
    private void isImageFiles(List<MultipartFile> fileList){
        for (MultipartFile file: fileList){
            if(!isImageFile(file)) throw new ImageException(NOT_IMAGE);
        }
    }
    private void validateMaxSize(Store store){
        if(store.getStoreImageList().size()>ImageMaxSize) throw new ImageException(MAXIMUM_IMAGE_SIZE);
    }
}
