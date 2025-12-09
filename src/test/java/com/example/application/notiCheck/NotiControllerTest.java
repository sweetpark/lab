package com.example.application.notiCheck;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(NotiController.class)
public class NotiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("노티 테스트")
    public void test() throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("msg","hello");
        map.put("count", 3);


        String json = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                post("/testNoti.do")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());

    }


    @Test
    @DisplayName("노티 불가 테스트")
    public void no_test() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("test", "key");
        String json = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                post("/testNotiNo.do")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isBadRequest());
    }

}