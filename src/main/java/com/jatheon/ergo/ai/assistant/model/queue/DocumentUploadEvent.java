package com.jatheon.ergo.ai.assistant.model.queue;

import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentUploadEvent implements Serializable {

    private String eventId;

    private String eventName;
    private String bucketName;
    private String objectKey;

}
