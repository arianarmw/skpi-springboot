package com.ariana.springboot.services;

public interface MinioService {
    String getPresignedUrl(String objectName);

    void removeImage(String objectName);

    void uploadImage(String objectName, byte[] bytes);
}
