package com.example.playground.cache;

import com.example.playground.cache.source.Repository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRepoConfig {
    @Bean
    public Repository repository(){
        return Mockito.mock(Repository.class);
    }
}
