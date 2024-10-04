1. PUT /embeddings
with mapping


[knn] queries cannot be provided directly, use the [knn] body parameter instead

Caused by: dev.langchain4j.store.embedding.elasticsearch.ElasticsearchRequestFailedException: co.elastic.clients.elasticsearch._types.ElasticsearchException: [es/search] failed: [illegal_argument_exception] [knn] queries cannot be provided directly, use the [knn] body parameter instead
at dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore.search(ElasticsearchEmbeddingStore.java:304)
at dev.langchain4j.store.embedding.EmbeddingStore.findRelevant(EmbeddingStore.java:157)
at com.jatheon.ergo.ai.assistant.service.OpenAIQuestionService.performSearch(OpenAIQuestionService.java:53)
at com.jatheon.ergo.ai.assistant.endpoint.QuestionController.question(QuestionController.java:31)


https://github.com/langchain4j/langchain4j-examples/blob/main/elasticsearch-example/src/main/java/ElasticsearchEmbeddingStoreExample.java


