package com.jatheon.ergo.ai.assistant.model.queue;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import software.amazon.awssdk.services.sqs.model.Message;

@Getter
@ToString
@EqualsAndHashCode
public class EventPayloadAdapter implements EventPayload {

    private final String eventId;
    private final String eventBody;

    public EventPayloadAdapter(final Message message) {
        this.eventId = message.receiptHandle();
        this.eventBody = message.body();
    }

}
