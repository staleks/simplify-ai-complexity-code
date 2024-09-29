package com.jatheon.ergo.ai.assistant.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jatheon.ergo.ai.assistant.service.queue.MessageEventGateway;
import com.jatheon.ergo.ai.assistant.service.queue.SQSMessageEventGateway;
import com.jatheon.ergo.ai.assistant.service.queue.parser.EventParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.Duration;

@Configuration
public class SQSConfig {

    @Value("${aws.sqs.accessKey}")
    private String accessKey;

    @Value("${aws.sqs.secretKey}")
    private String secretKey;

    @Value("${aws.sqs.region}")
    private String region;

    private AwsCredentialsProvider getCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean
    SqsClient sqsConsumerClient() {
        // Create an SQS client with custom configuration
        return SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(getCredentialsProvider())
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .retryStrategy(RetryMode.STANDARD)
                        .apiCallTimeout(Duration.ofMinutes(1))
                        .apiCallAttemptTimeout(Duration.ofSeconds(30))
                        .build())
                .build();
    }


    @Bean
    ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    EventParser eventParser(final ObjectMapper objectMapper) {
        return new EventParser(objectMapper);
    }

    @Bean
    MessageEventGateway messageEventGateway(final SqsClient sqsConsumerClient) {
        return new SQSMessageEventGateway(sqsConsumerClient);
    }

}
