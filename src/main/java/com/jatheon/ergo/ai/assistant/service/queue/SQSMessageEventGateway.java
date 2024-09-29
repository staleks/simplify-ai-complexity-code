package com.jatheon.ergo.ai.assistant.service.queue;

import com.jatheon.ergo.ai.assistant.model.queue.DocumentUploadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotificationRecord;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SQSMessageEventGateway implements MessageEventGateway {

    @Value("${aws.sqs.consumer.endpoint}")
    private String consumerEndpoint;

    @Value("${aws.sqs.consumer.maxNumberOfMessagesToReceive}")
    private Integer maxNumberOfMessages;

    @Value("${aws.sqs.consumer.waitTimeSeconds}")
    private Integer waitTimeSeconds;

    private final SqsClient sqsClient;

    @Override
    public List<DocumentUploadEvent> fetchEvents() {
        List<DocumentUploadEvent> documentUploadEvents = new ArrayList<>();
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(consumerEndpoint)
                .maxNumberOfMessages(maxNumberOfMessages)
                .waitTimeSeconds(waitTimeSeconds)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
        log.info("Received {} messages from SQS.", messages.size());
        for (Message message : messages) {
            documentUploadEvents = processS3Event(message);
        }
        return documentUploadEvents;
    }

    @Override
    public void deleteEvent(final String eventHandle) {
        log.info("Deleting event with handle: {} from SQS.", eventHandle);
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(consumerEndpoint)
                .receiptHandle(eventHandle)
                .build();
        try {
            sqsClient.deleteMessage(deleteRequest);
        } catch (final SqsException ex) {
            if (ex.statusCode() == 404) {
                log.warn("Event with handle: {} has already been deleted from SQS.", eventHandle, ex);
            } else {
                log.error("An error occurred while trying to delete event with handle: {} from SQS.", eventHandle, ex);
            }
        }
    }

    private List<DocumentUploadEvent> processS3Event(final Message message) {
        log.info("Processing S3 event... {}", message);
        List<DocumentUploadEvent> documentUploadEvents = new ArrayList<>();
        try {
            DocumentUploadEvent documentUploadEvent = new DocumentUploadEvent();
            log.info("Message ID: {}", message.messageId());
            log.info("Receipt Handle: {}", message.receiptHandle());
            documentUploadEvent.setEventId(message.receiptHandle());
            S3EventNotification s3EventNotification = S3EventNotification.fromJson(message.body());
            List<S3EventNotificationRecord> records = s3EventNotification.getRecords();
            for (S3EventNotificationRecord record : records) {
                String eventName = record.getEventName();
                documentUploadEvent.setEventName(eventName);
                String bucketName = record.getS3().getBucket().getName();
                documentUploadEvent.setBucketName(bucketName);
                String objectKey = record.getS3().getObject().getKey();
                documentUploadEvent.setObjectKey(objectKey);
                log.info("Processing S3 event: {}", eventName);
                log.info("Bucket: {}", bucketName);
                log.info("Object: {}", objectKey);
                log.info("--------------------");
                documentUploadEvents.add(documentUploadEvent);
            }
            log.info("Processed {} S3 events.", documentUploadEvents.size());
            return documentUploadEvents;
        } catch (Exception ex) {
            log.error("Error processing S3 event", ex);
            throw new IllegalStateException(ex);
        }
    }

}
