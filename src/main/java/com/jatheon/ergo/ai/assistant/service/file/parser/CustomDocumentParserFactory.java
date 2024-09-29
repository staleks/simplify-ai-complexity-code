package com.jatheon.ergo.ai.assistant.service.file.parser;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class CustomDocumentParserFactory implements DocumentParserFactory {

    private static final String CONTENT_TYPE_APPLICATION_PDF = "application/pdf";
    private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    @Override
    public DocumentParser create(final String contentType) {
        log.debug("Provided contentType: {}", contentType);
        if (StringUtils.isBlank(contentType)) {
            throw new IllegalArgumentException("ContentType must be provided!");
        }

        if (CONTENT_TYPE_APPLICATION_PDF.equalsIgnoreCase(contentType)) {
            log.info("Creating ApachePdfBoxDocumentParser ...");
            return new ApachePdfBoxDocumentParser();
        }

        if (CONTENT_TYPE_TEXT_PLAIN.equalsIgnoreCase(contentType)) {
            log.info("Creating TextDocumentParser ...");
            return new TextDocumentParser();
        }

        log.info("We couldn't detect better parser than ApacheTikaDocumentParser ... ");
        return new ApacheTikaDocumentParser();
    }

}
