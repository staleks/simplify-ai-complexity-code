package com.jatheon.ergo.ai.assistant.config.langchain4j;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class VectorStoreConfig {

    //~ Elasticsearch as VectorDB settings
    @Value("${elasticsearch.host}")
    private String esHost;
    @Value("${elasticsearch.port}")
    private String esPort;
    @Value("${elasticsearch.username}")
    private String esUsername;
    @Value("${elasticsearch.password}")
    private String esPassword;
    @Value("${vectorstore.index.name}")
    private String indexName;
    @Value("${vectorstore.dimension.size}")
    private Integer dimensionSize;

    @Bean
    EmbeddingStore<TextSegment> embeddingStore() {
        ElasticsearchEmbeddingStore embeddingStore = ElasticsearchEmbeddingStore.builder()
                .serverUrl("http://" + esHost + ":" + esPort)
                .userName(esUsername)
                .password(esPassword)
                .indexName(indexName)
                .dimension(dimensionSize)
                .build();
        log.debug("EmbeddingStore [ by ES ] client created.");
        return embeddingStore;
    }

}
