package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.model.inference.EnrichedQuestionResponse;
import com.jatheon.ergo.ai.assistant.model.inference.RecommendationItem;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;
import com.jatheon.ergo.ai.assistant.service.prompt.PromptFactory;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class EnrichedOpenAIQuestionService implements EnrichedQuestionService {

    @Value("${documentAssistant.inference.maxResults:20}")
    private Integer maxResults;

    @Value("${documentAssistant.inference.minScore:0.7}")
    private Double minScore;

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ChatLanguageModel chatLanguageModel;

    @Override
    public EnrichedQuestionResponse performAdvancedSearch(String question) throws QuestionServiceException {
        // Embed the question
        Response<Embedding> queryEmbedding = embeddingModel.embed(question);

        // Find relevant embeddings in embedding store by semantic similarity
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embeddingStore.findRelevant(queryEmbedding.content(), maxResults, minScore);
        List<RecommendationItem> recommendationItems = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> embeddingMatch : relevantEmbeddings) {
            RecommendationItem recommendationItem = new RecommendationItem();
            recommendationItem.setEmbeddingId(embeddingMatch.embeddingId());
            recommendationItem.setText(embeddingMatch.embedded().text());
            recommendationItem.setScore(embeddingMatch.score());
            recommendationItem.setResourceId(embeddingMatch.embedded().metadata("source"));
            recommendationItem.setLink(embeddingMatch.embedded().metadata("source"));
            recommendationItems.add(recommendationItem);
        }
        Collections.sort(recommendationItems);

        Prompt prompt = PromptFactory.ragPrompt(question, relevantEmbeddings);

        String answer = chatLanguageModel.generate(prompt.toUserMessage().text());
        // See an answer from the model
        return new EnrichedQuestionResponse(answer, recommendationItems);
    }

}
