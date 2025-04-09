package com.example.application.fileInputOutput.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("storage")
public class StorageProperties {

    private String location = "/webapp/upload";

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }
}
