package com.twogather.twogatherwebbackend.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.twogather.twogatherwebbackend.utils.ProcessUtils;
import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Slf4j
@Profile({"test"})
@Configuration
public class EmbeddedS3Config {
    @Value("${aws.s3.region}")
    private String region;
    @Value("${aws.s3.mock.port}")
    private int port;
    private S3Mock s3Mock;

    @Bean
    public S3Mock s3Mock() {
        s3Mock = new S3Mock.Builder()
                .withPort(port) // 해당 포트에 프로세스가 생성된다.
                .withInMemoryBackend() // 인메모리에서 활성화.
                .build();
        s3Mock.start();
        return s3Mock;
    }

    @PostConstruct
    public void init() {
        port = ProcessUtils.isRunningPort(port) ? ProcessUtils.findAvailableRandomPort() : port;
    }

    @PreDestroy
    public void destroyS3Mock() {
        this.s3Mock.stop();

        log.info("인메모리 S3 Mock 서버가 종료됩니다. port: {}", port);
    }

    @Bean
    public AmazonS3 amazonS3Client() {
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(getUri(), region);
        AmazonS3 client = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
        return client;
    }

    private String getUri() {
        return "http://localhost:" + port;
    }

}