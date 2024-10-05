package com.jatheon.ergo.ai.assistant.model.inference;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
public abstract class QuestionResponse {

    private final String answer;

    public QuestionResponse(String answer) {
        this.answer = answer;
    }
}
