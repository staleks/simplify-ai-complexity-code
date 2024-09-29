package com.jatheon.ergo.ai.assistant.service.file;

import com.jatheon.ergo.ai.assistant.model.file.DocumentMetadata;
import dev.langchain4j.data.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    void uploadFile(final MultipartFile file, final String fileName) throws IOException;

    DocumentMetadata fetchMetadata(final String location);

    Document load(final String location);

}
