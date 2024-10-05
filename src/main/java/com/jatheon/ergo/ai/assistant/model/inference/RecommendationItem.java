package com.jatheon.ergo.ai.assistant.model.inference;

import lombok.Data;

@Data
public class RecommendationItem implements Comparable<RecommendationItem> {

    private String text;
    private String resourceId;
    private String link;
    private double score;
    private String embeddingId;


    @Override
    public int compareTo(RecommendationItem o) {
        if (this.score == o.score) {
            return 0;
        }
        if (this.score < o.score) {
            return 1;
        }
        if (this.score > o.score) {
            return -1;
        }
        throw new RuntimeException("Invalid comparison");
    }

}
