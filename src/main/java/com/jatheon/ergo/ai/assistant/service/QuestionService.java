package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.model.inference.QuestionResponse;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;

public interface QuestionService {

    QuestionResponse performSearch(final String question) throws QuestionServiceException;
}
