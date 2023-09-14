package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Image;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.repository.ImageRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.junit.jupiter.api.Test;  // <-- 이 부분을 JUnit 5로 변경
import org.junit.jupiter.api.Assertions; // <-- 이 부분도 JUnit 5로 변경

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContexts;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
@Slf4j
public class StoreServiceTest {
    @Autowired
    ImageService imageService;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ImageRepository imageRepository;

/*
    @Test
    public void 동시_100_요청() throws InterruptedException, IOException {
        storeRepository.save(Store.builder().name("store1").build());

        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);
        File multipartFile = new File("src/test/resources/files/image.jpg");

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                        try {
                            imageService.upload(1l, convertFileToMultipartFile(multipartFile));

                        }catch(Exception e){
                            log.error(e.getMessage());
                            e.printStackTrace();
                        }
                        finally {
                            latch.countDown();
                        }
                    }
            );
        }

        latch.await();
        Integer size = imageRepository.findByStoreId(1L).size();

        System.out.println(size);
        Assertions.assertTrue(size > 0);
        Assertions.assertTrue(size < 12);

    }

    public static List<MultipartFile> convertFileToMultipartFile(File file) {
        List<MultipartFile> result = new ArrayList<>();
        FileInputStream fileInputStream = null;
        MultipartFile multipartFile = null;
        try {
            fileInputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile(
                    "file",
                    file.getName(),
                    "image/png",
                    fileInputStream
            );
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.add(multipartFile);
        return result;
    }
*/
}
