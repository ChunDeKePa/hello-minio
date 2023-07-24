package com.example.config;

import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Resource
    private MinioProperties minioProperties;

    public @Bean MyMinioClient myMinioClient() {
        MinioAsyncClient minioAsyncClient = MinioAsyncClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
        return new MyMinioClient(minioAsyncClient);
    }

    public @Bean MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
}
