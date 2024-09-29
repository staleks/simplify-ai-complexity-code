package com.jatheon.ergo.ai.assistant.service.file.parser;

import dev.langchain4j.data.document.DocumentParser;

public interface DocumentParserFactory {
    DocumentParser create(final String contentType);
}
