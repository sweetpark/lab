package com.example.playground.batch.source.tutorial.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class Tutorial01_JobParameter {

    /**
     * ========================================================================
     * JobParameter 란?
     * ========================================================================
     * Job 실행 시 외부에서 전달하는 파라미터
     * - 실행 시마다 다른 값을 주입 가능
     * - 실행 시각, 파일 경로, 조건값 등 동적 데이터 전달
     * 
     * 주요 타입: String, Long, Double, Date, LocalDate, LocalDateTime
     * 
     * 전달 방식:
     *   JobParameters params = new JobParametersBuilder()
     *       .addString("name", "value")
     *       .addLong("count", 100L)
     *       .toJobParameters();
     *   jobLauncher.run(job, params);
     * 
     * Bean 이름 규칙: tutorial01_jobParameterJob, tutorial01_jobParameterStep
     * ========================================================================
     */

    @Bean
    public Step tutorial01_jobParameterStep(JobRepository jobRepository,
                                            PlatformTransactionManager platformTransactionManager,
                                            @Value("#{jobParameters['userName']}") String userName,
                                            @Value("#{jobParameters['requestDate']}") String requestDate) {
        
        return new StepBuilder("tutorial01_jobParameterStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("=".repeat(60));
                System.out.println("[Tutorial01] JobParameter 예제");
                System.out.println("=".repeat(60));
                System.out.println("전달된 파라미터:");
                System.out.println("  - userName: " + userName);
                System.out.println("  - requestDate: " + requestDate);
                System.out.println("  - 현재 시각: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                System.out.println("-".repeat(60));
                System.out.println("JobParameter 사용법:");
                System.out.println("  1. @Value('#{jobParameters[\"key\"]}') 어노테이션 사용");
                System.out.println("  2. 타입 자동 변환 (String, Long, Date 등)");
                System.out.println("  3. Job 실행 시 JobParameters로 전달");
                System.out.println("=".repeat(60));
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    @Bean
    public Job tutorial01_jobParameterJob(JobRepository jobRepository, Step tutorial01_jobParameterStep) {
        return new JobBuilder("tutorial01_jobParameterJob", jobRepository)
            .start(tutorial01_jobParameterStep)
            .build();
    }
}
