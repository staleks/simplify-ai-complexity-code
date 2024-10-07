package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.model.inference.EnrichedQuestionResponse;
import com.jatheon.ergo.ai.assistant.model.inference.SimpleQuestionResponse;
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

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SimpleOpenAIQuestionService implements SimpleQuestionService {

    @Value("${documentAssistant.inference.maxResults:20}")
    private Integer maxResults;

    @Value("${documentAssistant.inference.minScore:0.7}")
    private Double minScore;

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ChatLanguageModel chatLanguageModel;

    @Override
    public SimpleQuestionResponse performSearch(String question) throws QuestionServiceException {
        // Create embedding from the question
        Response<Embedding> queryEmbedding = embeddingModel.embed(question);

        // Find relevant embeddings in Vector Store by semantic similarity
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embeddingStore.findRelevant(queryEmbedding.content(), maxResults, minScore);
        Prompt prompt = PromptFactory.ragPrompt(question, relevantEmbeddings);

        String answer = chatLanguageModel.generate(prompt.toUserMessage().text());

        //
        return new SimpleQuestionResponse(answer);
    }

}
