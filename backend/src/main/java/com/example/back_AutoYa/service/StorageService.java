package com.example.back_AutoYa.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final MinioClient minio;

    @Value("${minio.bucket}") private String bucket;
    @Value("${minio.public-url:}") private String publicBase;

    @PostConstruct
    public void ensureBucket() throws Exception {
        if (!minio.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minio.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    public String upload(String objectKey, MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            minio.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        }
        if (publicBase != null && !publicBase.isEmpty()) {
            return publicBase + "/" + bucket + "/" + objectKey;
        }
        return minio.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucket).object(objectKey).method(Method.GET)
                .expiry((int)Duration.ofDays(7).toSeconds()).build());
    }

    public String buildObjectKey(Long carId, String original) {
        String ext = null;
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.')+1);
        }
        String name = UUID.randomUUID().toString() + (ext==null?"":"."+ext);
        return "cars/"+carId+"/"+name;
    }
}
