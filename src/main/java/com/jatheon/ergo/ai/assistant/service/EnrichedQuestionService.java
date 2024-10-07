package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.model.inference.EnrichedQuestionResponse;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;

public interface EnrichedQuestionService {

    EnrichedQuestionResponse performAdvancedSearch(final String question) throws QuestionServiceException;

}
