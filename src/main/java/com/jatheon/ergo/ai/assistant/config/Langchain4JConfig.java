package com.jatheon.ergo.ai.assistant.config;

import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class Langchain4JConfig {
    private static final long TIMEOUT_SECONDS = 120;

    private static final Integer MAX_SEGMENT_SIZE_IN_CHARS = 500;
    private static final Integer MAX_OVERLAP_SIZE_IN_CHARS = 50;

    //~ OpenAI
    @Value("${langchain4j.chat-model.openai.api-key}")
    private String openAiApiKey;

    @Value("${langchain4j.chat-model.openai.model-name}")
    private String openAiModelName;

    @Value("${langchain4j.chat-model.openai.temperature}")
    private Double openAiTemperature;

    @Value("${langchain4j.chat-model.openai.top-p}")
    private Double openAiTopP;

    @Value("${langchain4j.chat-model.openai.max-tokens}")
    private Integer openAiMaxTokens;

    @Value("${langchain4j.chat-model.openai.presence-penalty}")
    private Double openAiPresencePenalty;

    @Value("${langchain4j.chat-model.openai.frequency-penalty}")
    private Double openAiFrequencyPenalty;

    @Value("${langchain4j.chat-model.openai.max-retries}")
    private Integer openAiMaxRetries;

    @Value("${langchain4j.chat-model.openai.log-requests}")
    private boolean openAiLogRequests;

    @Value("${langchain4j.chat-model.openai.log-responses}")
    private boolean openAiLogResponses;


    //~ Elasticsearch as VectorDB settings
    @Value("${elasticsearch.host}")
    private String esHost;
    @Value("${elasticsearch.port}")
    private String esPort;
    @Value("${vectorstore.index.name}")
    private String indexName;
    @Value("${vectorstore.dimension.size}")
    private Integer dimensionSize;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(openAiModelName)
                .temperature(openAiTemperature)
                .topP(openAiTopP)
                .maxTokens(openAiMaxTokens)
                .presencePenalty(openAiPresencePenalty)
                .frequencyPenalty(openAiFrequencyPenalty)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .maxRetries(openAiMaxRetries)
                .logRequests(openAiLogRequests)
                .logResponses(openAiLogResponses)
                .build();
    }

    /**
     * Embedding model using OpenAI.
     */
    @Bean
    EmbeddingModel embeddingModel() {
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                        .apiKey(openAiApiKey)
                        .modelName("text-embedding-ada-002")
                        .build();
        log.debug("EmbeddingModel [ by OpenAI ] client created.");
        return embeddingModel;
    }

    /**
     * Using ElasticSearch as vector DB.
     */
    @Bean
    EmbeddingStore<TextSegment> embeddingStore() {
        ElasticsearchEmbeddingStore embeddingStore = ElasticsearchEmbeddingStore.builder()
                .serverUrl("http://" + esHost + ":" + esPort)
                .indexName(indexName)
                .dimension(dimensionSize)
                .build();
        log.debug("EmbeddingStore [ by ES ] client created.");
        return embeddingStore;
    }

    @Bean
    EmbeddingStoreIngestor embeddingStoreIngestor(final EmbeddingModel embeddingModel,
                                                  final EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(MAX_SEGMENT_SIZE_IN_CHARS, MAX_OVERLAP_SIZE_IN_CHARS))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

}
