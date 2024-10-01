package com.jatheon.ergo.ai.assistant.config.langchain4j;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EmbeddingModelConfig {

    private static final String EMBEDDING_MODEL_NAME = "text-embedding-ada-002";

    //~ OpenAI
    @Value("${langchain4j.chat-model.openai.api-key}")
    private String openAiApiKey;

    @Bean
    EmbeddingModel embeddingModel() {
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(openAiApiKey)
                .modelName(EMBEDDING_MODEL_NAME)
                .build();
        log.debug("EmbeddingModel [ by OpenAI ] client created.");
        return embeddingModel;
    }

}
