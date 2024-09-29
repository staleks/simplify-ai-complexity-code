package com.jatheon.ergo.ai.assistant.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadStorageService {

    boolean uploadFile(final MultipartFile file, final String fileName) throws IOException;

}
