package com.example.playground.wiezon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    public boolean success;
    public String msg;
    public String column;
    public String value;
    public String encrypt;
    public String hash;
    public String password;
    public String otpPassword;

    public static Response fromOption(String option, String column, String value){
        if("-pw".equalsIgnoreCase(option) || "pw".equalsIgnoreCase(option)){
            return createPw(column, value);
        }
        return createDefault(column, value);
    }

    private static Response createDefault(String column, String value){
        Response response = new Response();
        response.success = true;
        response.msg = "success";
        response.column = column;
        response.value = value;
        response.encrypt = EncUtil.createEnc(value);
        response.hash = EncUtil.createHash(value);
        return response;
    }

    private static Response createPw(String column, String value){
        Response response = new Response();
        response.success = true;
        response.msg = "success";
        response.column = column;
        response.value = value;
        response.password = EncUtil.createEncPw(value);
        response.otpPassword = EncUtil.createEncOtp();
        return response;
    }

    public static Response fail(String msg, String column, String value){
        Response response = new Response();
        response.success = false;
        response.msg = msg;
        response.column = column;
        response.value = value;
        return response;
    }


    public static String out(Response response){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ObjectMapper 오류",e);
        }
    }
}
