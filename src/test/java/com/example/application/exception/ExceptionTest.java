package com.example.application.exception;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ExceptionController.class)
public class ExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testException() throws Exception{
        mockMvc.perform(get("/api/valid/exception"))
                .andExpect(status().is2xxSuccessful()) // 예외 발생 시 반환되는 상태 코드 확인
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomValidException))
                .andExpect(result -> assertEquals("validException", result.getResolvedException().getMessage()));


        mockMvc.perform(get("/api/custom/exception"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomException))
                .andExpect(result -> assertEquals("customException", result.getResolvedException().getMessage()));


        mockMvc.perform(get("/api/null/exception"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof  NullPointerException))
                .andExpect(result -> assertEquals("NullException", result.getResolvedException().getMessage()));
    }
}
