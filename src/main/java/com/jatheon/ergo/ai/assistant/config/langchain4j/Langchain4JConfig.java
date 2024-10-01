package com.jatheon.ergo.ai.assistant.config.langchain4j;

import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Slf4j
@Configuration
@Import({
        VectorStoreConfig.class,
        EmbeddingModelConfig.class,
        InferenceModelConfig.class
})
public class Langchain4JConfig {


    private static final Integer MAX_SEGMENT_SIZE_IN_CHARS = 500;
    private static final Integer MAX_OVERLAP_SIZE_IN_CHARS = 50;


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
