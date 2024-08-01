package com.ariana.springboot.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.ariana.springboot.services.MinioService;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;

@Service
@PropertySource("classpath:application.properties")
public class MinioServiceImpl implements MinioService {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket.name}")
    private String bucketName;

    private MinioClient minioClient;

    @PostConstruct
    private void init() {
        minioClient = MinioClient.builder().endpoint(
                url).credentials(
                        accessKey,
                        secretKey)
                .build();
    }

    @Override
    public String getPresignedUrl(String objectName) {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .expiry(24 * 60 * 60)
                .build();
        try {
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void removeImage(String objectName) {
    }

    @Override
    public void uploadImage(String objectName, byte[] bytes) {
    }
}