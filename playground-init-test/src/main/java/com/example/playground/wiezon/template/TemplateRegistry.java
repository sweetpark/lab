package com.example.playground.wiezon.template;

import com.example.playground.wiezon.Enum.Division;
import com.example.playground.wiezon.context.TemplateContext;
import com.example.playground.wiezon.exception.FileParseException;
import com.example.playground.wiezon.service.FileReadService;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class TemplateRegistry {

    private final FileReadService fileReadService;;
    private final ResourcePatternResolver resolver;
    private List<TemplateContext> templateContextList = new ArrayList<>();
    private Resource[] templates;

    public TemplateRegistry(FileReadService fileReadService, ResourcePatternResolver resolver) {
        this.fileReadService = fileReadService;
        this.resolver        = resolver;
    }

    public TemplateRegistry loadAll() throws IOException {
        templates = resolver.getResources("classpath:/data/**/*.json");
        Arrays.stream(templates).forEach(file -> {
            try{
                templateContextList.add(fileReadService.parseJson(file.getInputStream()));
            }catch(Exception e){
                throw new FileParseException("Failed to parse json file: " + file, e);
            }

        });
        return this;
    }


    public List<TemplateContext> getTemplates(Division division) {
        return templateContextList.stream()
                .filter(d -> division.name().equalsIgnoreCase(d.getDivision()))
                .toList();
    }

}
