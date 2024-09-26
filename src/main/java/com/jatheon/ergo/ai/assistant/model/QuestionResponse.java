package com.jatheon.ergo.ai.assistant.model;

import lombok.Builder;
import lombok.Getter;


@Builder
public class QuestionResponse {
    @Getter
    private final String answer;
}
