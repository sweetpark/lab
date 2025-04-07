package com.example.application.fileInputOutput.controller;

import application.file.exception.StorageFileNotFoundException;
import application.file.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.stream.Collectors;


@Controller
public class FileController {

    private final StorageService service;

    @Autowired
    public FileController(StorageService service){
        this.service = service;
    }


    @GetMapping("/")
    public String listFiles(Model model) throws IOException {
        model.addAttribute("files", service.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "list.jsp";
    }

    @GetMapping("/files/{id}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable("id")Long id, Model model){

        Resource file = service.loadResource(id);


        if(file == null)
            return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", HttpHeaders.CONTENT_DISPOSITION+"attachment; filename=\"" + file.getFilename() + "\"");

        return ResponseEntity.ok().headers(headers).body(file);
    }

    @PostMapping
    public String fileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        service.save(file);
        redirectAttributes.addFlashAttribute("message" , "success upload" + file.getOriginalFilename() + "!");

        try{
            Resource resource = new UrlResource("http://localhost/data.txt");
            InputStream in = resource.getInputStream();
            while( in.read() != -1) {
                System.out.println("test");
            }
        }catch (MalformedURLException e) {

        }catch (IOException e){

        }

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException ex){
        return ResponseEntity.notFound().build();
    }


}
