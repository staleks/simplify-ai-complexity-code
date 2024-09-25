package com.jatheon.ergo.ai.assistant.service.error;

public class QuestionServiceException extends RuntimeException {

    private static final long serialVersionUID = -3327198147952275638L;

    public QuestionServiceException(final String message) {
        super(message);
    }

    public QuestionServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
