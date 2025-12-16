package com.example.playground.exterior.src;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
@Slf4j
@Component
public class PropertiesRead {

    private String url;
    private String apiKey;

    @PostConstruct
    public void load(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("C:/p_wy/wiz_project/5.HOME_NET/5.CVNET_DALIM/conf/server.properties"));
            String line;
            while((line = br.readLine()) != null){
                if(line.contains("server.ip")){
                    String[] ip = line.split("=");
                    //wss://183.98.244.118:22000/thirdapi/v1/ev
                    this.url = "wss://" + ip[1].trim() +":22000/thirdapi/v1/ev";
                }else if(line.contains("server.apikey")){
                    String[] apikey = line.split("=");
                    this.apiKey = apikey[1].trim();
                }
            }
        }catch(FileNotFoundException e){
            log.error("[FAIL] Not Found server.properties : {}", e.getMessage());
        }catch(IOException e){
            log.error("[FAIL] server.properties Read Error: {}", e.getMessage());
        }
    }



    public void printProperties(){
        System.out.println("SERVER URL : " + url);
        System.out.println("API KEY : " + apiKey);
    }

}
