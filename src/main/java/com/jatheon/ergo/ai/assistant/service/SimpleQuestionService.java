package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.model.inference.SimpleQuestionResponse;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;

public interface SimpleQuestionService {

    SimpleQuestionResponse performSearch(final String question) throws QuestionServiceException;

}
