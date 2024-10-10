package com.jatheon.ergo.ai.assistant.endpoint;

import com.jatheon.ergo.ai.assistant.model.image.ImageRequest;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;
import com.jatheon.ergo.ai.assistant.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ImageController {

    private static final String IMAGE_ENDPOINT = "/ai/get-image";

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        log.info("sasa");
        this.imageService = imageService;
    }

    @PostMapping(value = IMAGE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateImage(@RequestBody ImageRequest request) {
        log.info("generate image");
        if (StringUtils.isNotBlank(request.getPrompt())) {
            try {
                return ResponseEntity.ok(imageService.generateImage(request.getPrompt()));
            } catch (QuestionServiceException qse) {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
