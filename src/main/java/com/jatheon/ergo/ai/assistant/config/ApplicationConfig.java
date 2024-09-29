package com.jatheon.ergo.ai.assistant.config;

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
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.amazon.s3.AmazonS3DocumentLoader;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Qualifier;
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
    QuestionService questionService(final ChatLanguageModel chatLanguageModel) {
        return new OpenAIQuestionService(new PromptFactory(), chatLanguageModel);
    }

    @Bean
    QuestionController questionController(final QuestionService questionService) {
        return new QuestionController(questionService);
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
                                                final StorageService storageService) {
        return new IngestionOrchestrator(messageEventGateway, storageService);
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
