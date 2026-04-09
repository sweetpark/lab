package com.example.playground.batch.source.tutorial.tasklet;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class Tutorial04_Tasklet {

    /**
     * ========================================================================
     * Tasklet 란?
     * ========================================================================
     * 단일 작업 단위를 표현하는 인터페이스
     * - execute() 메서드 하나만 구현
     * - RepeatStatus 반환으로 반복 제어
     * 
     * RepeatStatus:
     * - FINISHED: 작업 완료, 더 이상 반복 안함
     * - CONTINUABLE: 작업 반복 (예: 파일 처리 완료 전까지)
     * 
     * 사용 패턴:
     * 1. 익명 클래스/람다: 간단한 작업
     * 2. 별도 클래스: 복잡한 로직 분리
     * 3. @StepScope: Step 실행 시점에 Bean 생성
     * 
     * 트랜잭션:
     * - PlatformTransactionManager 주입 필요
     * - 예외 발생 시 자동 롤백
     * ========================================================================
     */

    // ===== 기본 Tasklet (람다) =====

    @Bean
    public Tasklet tutorial04_simpleTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("=".repeat(60));
            System.out.println("[Tutorial04] Tasklet 예제");
            System.out.println("=".repeat(60));
            System.out.println("[Tasklet 1] 단순 Tasklet 실행");
            System.out.println("Step Context에서 데이터 읽기:");
            System.out.println("  Step Name: " + chunkContext.getStepContext().getStepName());
            System.out.println("  Job Name: " + chunkContext.getStepContext().getJobName());
            System.out.println("=".repeat(60));
            return RepeatStatus.FINISHED;
        };
    }

    // ===== 파일 처리 Tasklet =====

    @Bean
    public Tasklet tutorial04_fileProcessTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("-".repeat(60));
            System.out.println("[Tasklet 2] 파일 처리 Tasklet");
            System.out.println("-".repeat(60));
            System.out.println("Tasklet 사용 시나리오:");
            System.out.println("  1. 파일 읽기/쓰기 작업");
            System.out.println("  2. 데이터베이스 직접 조작");
            System.out.println("  3. 외부 API 호출");
            System.out.println("  4. 리소스 정리 (cleanup)");
            System.out.println("-".repeat(60));
            System.out.println("Tasklet vs Chunk:");
            System.out.println("  Tasklet: 단일 작업, 복잡한 로직");
            System.out.println("  Chunk: 대량 데이터 처리, 반복 패턴");
            return RepeatStatus.FINISHED;
        };
    }

    // ===== Step 정의 =====

    @Bean
    public Step tutorial04_firstStep(JobRepository jobRepository,
                                      PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial04_firstStep", jobRepository)
            .tasklet(tutorial04_simpleTasklet(), platformTransactionManager)
            .build();
    }

    @Bean
    public Step tutorial04_secondStep(JobRepository jobRepository,
                                       PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial04_secondStep", jobRepository)
            .tasklet(tutorial04_fileProcessTasklet(), platformTransactionManager)
            .build();
    }

    // ===== Job 정의 =====

    @Bean
    public Job tutorial04_taskletJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial04_taskletJob", jobRepository)
            .start(tutorial04_firstStep(jobRepository, null))
            .next(tutorial04_secondStep(jobRepository, null))
            .build();
    }
}
