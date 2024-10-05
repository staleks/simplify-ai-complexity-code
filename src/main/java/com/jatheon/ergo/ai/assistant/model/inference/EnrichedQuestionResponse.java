package com.jatheon.ergo.ai.assistant.model.inference;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class EnrichedQuestionResponse extends SimpleQuestionResponse {

    @Getter
    private List<RecommendationItem> recommendations = new ArrayList<>();

    public EnrichedQuestionResponse(final String answer,
                                    final List<RecommendationItem> recommendations) {
        super(answer);
        this.recommendations = recommendations;
    }

}
