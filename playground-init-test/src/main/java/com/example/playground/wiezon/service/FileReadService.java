package com.example.playground.wiezon.service;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileReadService {


    public Map<String, Object> parseJson(Path path){

        Map<String, Object> result = new HashMap<>();

        try(BufferedReader inputStream = new BufferedReader(new InputStreamReader(Files.newInputStream(path), "UTF-8"))) {
            Gson gson = new Gson();
            result = gson.fromJson(inputStream, Map.class);

            System.out.println(result);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read json file : " + path, e);
        }


        return result;
    }
}
