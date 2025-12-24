package com.example.playground.batch.source;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.UUID;

@Configuration
public class HelloWorldConfiguration {

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("Step1", jobRepository).allowStartIfComplete(true).tasklet((contribution, chunkContext) -> {
            System.out.println("Hello World");
            return RepeatStatus.FINISHED;
        }, platformTransactionManager).build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder("helloWorld", jobRepository).start(step).build();
    }
}
