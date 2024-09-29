package com.jatheon.ergo.ai.assistant.model.file;

import lombok.Value;

@Value(staticConstructor = "of")
public class DocumentMetadata {
    Long contentLength;
    String contentType;
}
