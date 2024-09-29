package com.jatheon.ergo.ai.assistant.model.queue;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class DocumentUploadEvent implements Serializable {

    @Setter
    private String eventId;

}
