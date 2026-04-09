package com.example.playground.batch.source.tutorial.step;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class Tutorial03_Step {

    /**
     * ========================================================================
     * Step 란?
     * ========================================================================
     * Job을 구성하는 작업의 단위
     * - Tasklet 기반: 단일 작업 반복 실행
     * - Chunk 기반: 아이템 목록 처리 (Reader -> Processor -> Writer)
     * 
     * Step 구성 요소:
     * - Tasklet: 실제 작업 로직
     * - ChunkSize: 한 번에 처리할 아이템 수
     * - Skip/Retry: 오류 처리 정책
     * - Listeners: 실행 전후 훅
     * 
     * Step 실행 옵션:
     * - allowStartIfComplete: 완료 후에도 재실행 허용
     * - startLimit: 최대 실행 횟수 제한
     * - chunk(size): Chunk 기반 처리 설정
     * ========================================================================
     */

    // ===== Tasklet 기반 Step =====

    @Bean
    public Step tutorial03_taskletStep(JobRepository jobRepository,
                                        PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial03_taskletStep", jobRepository)
            .allowStartIfComplete(true)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("=".repeat(60));
                System.out.println("[Tutorial03] Tasklet 기반 Step 예제");
                System.out.println("=".repeat(60));
                System.out.println("Step Name: " + chunkContext.getStepContext().getStepName());
                System.out.println("Job Name: " + chunkContext.getStepContext().getJobName());
                System.out.println("-".repeat(60));
                System.out.println("Tasklet 특징:");
                System.out.println("  - 단일 작업 단위");
                System.out.println("  - RepeatStatus.FINISHED로 종료");
                System.out.println("  - RepeatStatus.CONTINUABLE로 재실행 가능");
                System.out.println("  - 직접 트랜잭션 관리 가능");
                System.out.println("=".repeat(60));
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    // ===== Chunk 기반 Step =====

    @Bean
    public ItemReader<Integer> tutorial03_itemReader() {
        return new ItemReader<Integer>() {
            private int count = 0;
            @Override
            public Integer read() {
                if (count > 15) return null;
                return count++;
            }
        };
    }

    @Bean
    public Step tutorial03_chunkStep(JobRepository jobRepository,
                                      PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial03_chunkStep", jobRepository)
            .<Integer, Integer>chunk(5)
            .reader(tutorial03_itemReader())
            .processor((item) -> {
                System.out.println("[Processor] 처리 중: " + item + " -> " + (item * 2));
                return item * 2;
            })
            .writer((ItemWriter<Integer>) items -> {
                System.out.println("[Writer] 배치 기록: " + items);
            })
            .build();
    }

    // ===== Job 정의 =====

    @Bean
    public Job tutorial03_stepJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial03_stepJob", jobRepository)
            .start(tutorial03_taskletStep(jobRepository, null))
            .next(tutorial03_chunkStep(jobRepository, null))
            .build();
    }
}
