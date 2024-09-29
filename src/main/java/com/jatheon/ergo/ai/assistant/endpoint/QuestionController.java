package com.jatheon.ergo.ai.assistant.endpoint;

import com.jatheon.ergo.ai.assistant.model.inference.QuestionRequest;
import com.jatheon.ergo.ai.assistant.model.inference.QuestionResponse;
import com.jatheon.ergo.ai.assistant.service.QuestionService;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;
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
@RequiredArgsConstructor
public class QuestionController {

    private static final String POST_QUESTION_ENDPOINT = "/ai/get-answer";

    private final QuestionService questionService;

    @PostMapping(value = POST_QUESTION_ENDPOINT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionResponse> question(@RequestBody QuestionRequest request) {
        if (StringUtils.isNotBlank(request.getQuestion())) {
            try {
                return ResponseEntity.ok(questionService.performSearch(request.getQuestion()));
            } catch (QuestionServiceException qse) {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
