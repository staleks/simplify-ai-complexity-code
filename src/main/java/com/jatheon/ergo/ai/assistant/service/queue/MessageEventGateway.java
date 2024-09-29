package com.jatheon.ergo.ai.assistant.service.queue;

import dev.ai4j.openai4j.chat.Message;

public interface MessageEventGateway {

    void fetchEvents();
    void deleteEvent(final String eventHandle);
}
