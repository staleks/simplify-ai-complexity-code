package com.jatheon.ergo.ai.assistant.config;

import com.jatheon.ergo.ai.assistant.endpoint.QuestionController;
import com.jatheon.ergo.ai.assistant.endpoint.file.FileUploadController;
import com.jatheon.ergo.ai.assistant.service.OpenAIQuestionService;
import com.jatheon.ergo.ai.assistant.service.QuestionService;
import com.jatheon.ergo.ai.assistant.service.file.S3UploadStorageService;
import com.jatheon.ergo.ai.assistant.service.file.UploadStorageService;
import com.jatheon.ergo.ai.assistant.service.prompt.PromptFactory;
import com.jatheon.ergo.ai.assistant.service.queue.MessageEventGateway;
import com.jatheon.ergo.ai.assistant.service.queue.SQSMessageEventGateway;
import dev.langchain4j.model.chat.ChatLanguageModel;
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
    UploadStorageService uploadStorageService(final S3Client s3Client) {
        return new S3UploadStorageService(s3Client);
    }

    @Bean
    FileUploadController fileUploadController(final UploadStorageService uploadStorageService) {
        return new FileUploadController(uploadStorageService);
    }

    //~ upload S3 event
    @Bean
    MessageEventGateway messageEventGateway(final SqsClient sqsConsumerClient) {
        return new SQSMessageEventGateway(sqsConsumerClient);
    }

}
