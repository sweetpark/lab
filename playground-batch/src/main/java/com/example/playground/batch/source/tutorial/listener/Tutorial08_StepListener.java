package com.example.playground.batch.source.tutorial.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;

@Configuration
public class Tutorial08_StepListener {

    /**
     * ========================================================================
     * StepExecutionListener 란?
     * ========================================================================
     * Step 실행 전후에 실행되는 콜백
     * 
     * 메서드:
     * - beforeStep(): Step 시작 전
     * - afterStep(): Step 종료 후 (성공/실패 상관없이)
     * 
     * StepExecution에서 사용 가능한 정보:
     * - getStepName(): Step 이름
     * - getReadCount(), getWriteCount(): 읽기/쓰기 건수
     * - getSkipCount(): 건너뛴 건수
     * - getCommitCount(), getRollbackCount(): 트랜잭션 횟수
     * - getExecutionContext(): Step 간 데이터 공유
     * 
     * ExitStatus 변경:
     * - afterStep에서 반환값으로 상태 변경 가능
     * ========================================================================
     */

    // ===== Listener 정의 =====

    @Bean
    public StepExecutionListener tutorial08_stepListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                System.out.println("-".repeat(60));
                System.out.println("[StepListener] Step 시작 전 - beforeStep()");
                System.out.println("Step Name: " + stepExecution.getStepName());
                System.out.println("Job Name: " + stepExecution.getJobExecution().getJobInstance().getJobName());
                System.out.println("-".repeat(60));
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                System.out.println("-".repeat(60));
                System.out.println("[StepListener] Step 종료 후 - afterStep()");
                System.out.println("-".repeat(60));
                System.out.println("Step Name: " + stepExecution.getStepName());
                System.out.println("Status: " + stepExecution.getStatus());
                
                // 처리 통계
                System.out.println("Processing Statistics:");
                System.out.println("  - Read Count: " + stepExecution.getReadCount());
                System.out.println("  - Write Count: " + stepExecution.getWriteCount());
                System.out.println("  - Skip Count: " + stepExecution.getSkipCount());
                System.out.println("  - Filter Count: " + stepExecution.getFilterCount());
                System.out.println("  - Commit Count: " + stepExecution.getCommitCount());
                System.out.println("  - Rollback Count: " + stepExecution.getRollbackCount());
                
                // 소요 시간
                if (stepExecution.getStartTime() != null && stepExecution.getEndTime() != null) {
                    Duration duration = Duration.between(
                        stepExecution.getStartTime(), 
                        stepExecution.getEndTime()
                    );
                    System.out.println("  - Duration: " + duration.toMillis() + "ms");
                }
                System.out.println("-".repeat(60));
                
                return stepExecution.getExitStatus();
            }
        };
    }

    // ===== Step 정의 (Chunk 기반 통계 확인) =====

    @Bean
    public ItemReader<Integer> tutorial08_itemReader() {
        return new ItemReader<Integer>() {
            private int count = 0;
            @Override
            public Integer read() {
                if (count > 8) return null;
                return count++;
            }
        };
    }

    @Bean
    public Step tutorial08_stepListenerStep(JobRepository jobRepository,
                                             PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial08_stepListenerStep", jobRepository)
            .listener(tutorial08_stepListener())
            .<Integer, Integer>chunk(3)
            .reader(tutorial08_itemReader())
            .processor(item -> {
                System.out.println("[Process] " + item + " -> " + (item * 10));
                return item * 10;
            })
            .writer(items -> {
                System.out.println("[Write] " + items);
            })
            .build();
    }

    // ===== Job =====

    @Bean
    public Job tutorial08_stepListenerJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial08_stepListenerJob", jobRepository)
            .start(tutorial08_stepListenerStep(jobRepository, null))
            .build();
    }
}
