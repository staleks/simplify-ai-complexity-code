package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.model.inference.EnrichedQuestionResponse;
import com.jatheon.ergo.ai.assistant.model.inference.SimpleQuestionResponse;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;

public interface QuestionService {

    SimpleQuestionResponse performSearch(final String question) throws QuestionServiceException;

    EnrichedQuestionResponse performAdvancedSearch(final String question) throws QuestionServiceException;
}
