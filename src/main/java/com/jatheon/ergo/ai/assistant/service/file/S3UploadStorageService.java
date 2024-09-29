package com.jatheon.ergo.ai.assistant.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class S3UploadStorageService implements UploadStorageService {

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private final S3Client s3Client;

    @Override
    public boolean uploadFile(MultipartFile file, String fileName) throws IOException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return true;
        } catch (S3Exception e) {
            throw new IOException("Error uploading file to S3", e);
        }
    }
}
