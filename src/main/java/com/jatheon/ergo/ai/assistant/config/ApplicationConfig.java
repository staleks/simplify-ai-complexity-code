package com.jatheon.ergo.ai.assistant.config;

import com.jatheon.ergo.ai.assistant.config.langchain4j.Langchain4JConfig;
import com.jatheon.ergo.ai.assistant.config.queue.SQSConfig;
import com.jatheon.ergo.ai.assistant.config.scheduling.SchedulerConfig;
import com.jatheon.ergo.ai.assistant.config.storage.S3ClientConfig;
import com.jatheon.ergo.ai.assistant.config.web.RestWebMvcConfig;
import com.jatheon.ergo.ai.assistant.endpoint.AdvancedQuestionController;
import com.jatheon.ergo.ai.assistant.endpoint.QuestionController;
import com.jatheon.ergo.ai.assistant.endpoint.file.FileUploadController;
import com.jatheon.ergo.ai.assistant.service.IngestionOrchestrator;
import com.jatheon.ergo.ai.assistant.service.OpenAIQuestionService;
import com.jatheon.ergo.ai.assistant.service.QuestionService;
import com.jatheon.ergo.ai.assistant.service.file.S3StorageService;
import com.jatheon.ergo.ai.assistant.service.file.StorageService;
import com.jatheon.ergo.ai.assistant.service.file.parser.CustomDocumentParserFactory;
import com.jatheon.ergo.ai.assistant.service.file.parser.DocumentParserFactory;
import com.jatheon.ergo.ai.assistant.service.prompt.PromptFactory;
import com.jatheon.ergo.ai.assistant.service.queue.MessageEventGateway;
import com.jatheon.ergo.ai.assistant.service.queue.SQSMessageEventGateway;
import dev.langchain4j.data.document.loader.amazon.s3.AmazonS3DocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

@Import({
        RestWebMvcConfig.class,
        Langchain4JConfig.class,
        S3ClientConfig.class,
        SchedulerConfig.class,
        SQSConfig.class
})
@Configuration
public class ApplicationConfig {

    @Bean
    QuestionService questionService(final EmbeddingModel embeddingModel,
                                    final EmbeddingStore<TextSegment> embeddingStore,
                                    final ChatLanguageModel chatLanguageModel) {
        return new OpenAIQuestionService(embeddingModel, embeddingStore, chatLanguageModel);
    }

    @Bean
    QuestionController questionController(final QuestionService questionService) {
        return new QuestionController(questionService);
    }

    @Bean
    AdvancedQuestionController advancedQuestionController(final QuestionService questionService) {
        return new AdvancedQuestionController(questionService);
    }

    //~ file upload
    @Bean
    StorageService uploadStorageService(final S3Client s3Client,
                                        final DocumentParserFactory documentParserFactory,
                                        final AmazonS3DocumentLoader amazonS3DocumentLoader) {
        return new S3StorageService(s3Client, documentParserFactory, amazonS3DocumentLoader);
    }

    @Bean
    FileUploadController fileUploadController(final StorageService uploadStorageService) {
        return new FileUploadController(uploadStorageService);
    }

    //~ upload S3 event
    @Bean
    MessageEventGateway messageEventGateway(final SqsClient sqsConsumerClient) {
        return new SQSMessageEventGateway(sqsConsumerClient);
    }

    @Bean
    IngestionOrchestrator ingestionOrchestrator(final MessageEventGateway messageEventGateway,
                                                final StorageService storageService,
                                                final EmbeddingStoreIngestor embeddingStoreIngestor) {
        return new IngestionOrchestrator(messageEventGateway, storageService, embeddingStoreIngestor);
    }

    //~ document parsing
    @Bean
    DocumentParserFactory documentParser() {
        return new CustomDocumentParserFactory();
    }

    @Bean
    AmazonS3DocumentLoader documentLoader(final S3Client s3Client) {
        return new AmazonS3DocumentLoader(s3Client);
    }

}
