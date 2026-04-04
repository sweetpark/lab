package com.example.playground.wiezon.template;

import com.example.playground.wiezon.context.TemplateContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemplateLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseDir;

    public TemplateLoader(String baseDir) {
        this.baseDir = baseDir;
    }

    public TemplateContext load(String relativePath) throws IOException {
        Path filePath = Path.of(baseDir, relativePath);
        System.out.println("Trying to load: " + filePath.toAbsolutePath());
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("파일이 없습니다 : " + filePath.toAbsolutePath());
        }
        return objectMapper.readValue(Files.readString(filePath), new TypeReference<>() {
        });
    }
}
