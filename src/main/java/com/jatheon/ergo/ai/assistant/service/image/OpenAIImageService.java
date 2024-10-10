package com.jatheon.ergo.ai.assistant.service.image;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import static dev.langchain4j.model.openai.OpenAiImageModelName.DALL_E_3;

@Slf4j
public class OpenAIImageService implements ImageService {

    @Value("${langchain4j.dalle3.api-key}")
    private String openAiApiKey;

    @Override
    public String generateImage(final String prompt) {
        log.info("generate image");
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(openAiApiKey)
                .modelName(DALL_E_3)
                .logRequests(true)
                .logResponses(true)
                .build();
        Response<Image> response = model.generate(prompt);
        return response.content().url().toString();
    }
}
