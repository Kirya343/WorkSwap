package org.workswap.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.storage.config.S3Properties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@RequiredArgsConstructor
@Service
@Profile("server")
public class S3StorageService {

    private final S3Client s3Client;

    private final S3Properties s3Properties;
    
    public String upload(
        InputStream inputStream,
        String fileName,
        long size,
        String contentType,
        String dir
    ) throws IOException {

        String newFileName = fileName != null ? fileName : UUID.randomUUID().toString();

        String key = dir + "/" + newFileName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(key)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(inputStream, size)
        );

        return key;
    }

    public void delete(String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(objectKey)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (NoSuchKeyException e) {
            // файл уже отсутствует — ничего не делаем
        }
    }

    public List<S3Object> listModpackFiles(String prefix) {

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(s3Properties.bucket())
                .prefix(prefix)
                .build();

        return s3Client.listObjectsV2(request).contents();
    }
}
