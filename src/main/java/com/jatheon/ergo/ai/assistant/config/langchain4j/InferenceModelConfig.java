package com.jatheon.ergo.ai.assistant.config.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class InferenceModelConfig {
    private static final long TIMEOUT_SECONDS = 120;

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

}
