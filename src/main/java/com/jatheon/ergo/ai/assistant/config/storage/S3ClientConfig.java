package com.jatheon.ergo.ai.assistant.config.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;

@Configuration
public class S3ClientConfig {

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Value("${aws.s3.endpoint}")
    private String serviceEndpoint;

    @Value("${aws.s3.region}")
    private String regionName;

    private AwsCredentialsProvider getCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean
    S3Client s3Client() {
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(Boolean.TRUE)
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();

        return S3Client.builder()
                .serviceConfiguration(s3Configuration)
                .credentialsProvider(getCredentialsProvider())
                .region(software.amazon.awssdk.regions.Region.of(regionName))
                .endpointOverride(URI.create(serviceEndpoint))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .retryStrategy(RetryMode.STANDARD)
                        .apiCallTimeout(Duration.ofMinutes(1))
                        .apiCallAttemptTimeout(Duration.ofSeconds(30))
                        .build())
                .build();
    }

}
