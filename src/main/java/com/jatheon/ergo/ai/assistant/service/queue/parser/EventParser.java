package com.jatheon.ergo.ai.assistant.service.queue.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jatheon.ergo.ai.assistant.model.queue.DocumentUploadEvent;
import com.jatheon.ergo.ai.assistant.model.queue.EventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class EventParser {

    private final ObjectMapper objectMapper;

    public DocumentUploadEvent parseEvent(final EventPayload payload) {
        try {
            DocumentUploadEvent documentUploadEvent = objectMapper.readValue(payload.getEventBody(), DocumentUploadEvent.class);
            documentUploadEvent.setEventId(payload.getEventId());
            return documentUploadEvent;
        } catch(IOException e) {
            log.error("Error processing event message", e);
            throw new IllegalStateException(e.getMessage());
        }
    }

}
