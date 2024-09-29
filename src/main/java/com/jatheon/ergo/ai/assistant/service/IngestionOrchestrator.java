package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.service.queue.MessageEventGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class IngestionOrchestrator {

    private final MessageEventGateway messageEventGateway;

    /**
    public void processEvent() {
        messageEventGateway.fetchEvents();.forEach(message -> {
            log.info("Received message: {}", message);
            // Process the message
            messageEventGateway.deleteMessage(message);
            log.info("Deleted message: {}", message);
        });
    }**/

    @Scheduled(fixedRateString = "${processing.message.fetch.rate}")
    public void processEvent() {
        messageEventGateway.fetchEvents();
    }

}
