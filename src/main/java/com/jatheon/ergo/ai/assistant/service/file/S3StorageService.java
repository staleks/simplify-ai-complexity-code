package com.jatheon.ergo.ai.assistant.service.file;

import com.jatheon.ergo.ai.assistant.model.file.DocumentMetadata;
import com.jatheon.ergo.ai.assistant.service.file.parser.DocumentParserFactory;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.amazon.s3.AmazonS3DocumentLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private final S3Client s3Client;
    private final DocumentParserFactory documentParserFactory;
    private final AmazonS3DocumentLoader documentLoader;

    @Override
    public void uploadFile(MultipartFile file, String fileName) throws IOException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("File uploaded successfully to S3: {}", fileName);
        } catch (S3Exception e) {
            throw new IOException("Error uploading file to S3", e);
        }
    }

    @Override
    public DocumentMetadata fetchMetadata(final String location) {
        log.debug("loading object metadata [location: {}]", location);
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(location)
                    .build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            return DocumentMetadata.of(headObjectResponse.contentLength(), headObjectResponse.contentType());
        } catch(final S3Exception s3Exception) {
            if (s3Exception.statusCode() == 404) {
                throw new IllegalArgumentException("Content with specified key does not exist.", s3Exception);
            }
            throw new IllegalArgumentException(s3Exception);
        } catch (final Exception exception) {
            throw new IllegalArgumentException("Unable to read message content from storage.", exception);
        }

    }

    @Override
    public Document load(final String location) {
        try {
            DocumentMetadata attachmentMetadata = fetchMetadata(location);
            log.debug("AttachmentMetadata.contentType: {}", attachmentMetadata.getContentType());
            log.debug("AttachmentMetadata.contentLength: {}", attachmentMetadata.getContentLength());
            DocumentParser documentParser = documentParserFactory.create(attachmentMetadata.getContentType());
            Document document = documentLoader.loadDocument(bucketName, location, documentParser);
            document.metadata().put("contentLength", attachmentMetadata.getContentLength());
            document.metadata().put("contentType", attachmentMetadata.getContentType());
            return document;
        } catch(final S3Exception s3Exception) {
            final String message = format("Unable to read message content from AWS storage [bucket: %s, key: %s]", bucketName, location);
            if (s3Exception.statusCode() == 404) {
                throw new IllegalStateException("Content with specified key does not exist.", s3Exception);
            }
            throw new IllegalArgumentException(s3Exception);
        } catch (final Exception exception) {
            throw new IllegalArgumentException("Unable to read message content from storage.", exception);
        }
    }


}
