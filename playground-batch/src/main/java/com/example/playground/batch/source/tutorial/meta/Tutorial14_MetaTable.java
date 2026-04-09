package com.example.playground.batch.source.tutorial.meta;

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
public class Tutorial14_MetaTable {

    /**
     * ========================================================================
     * Spring Batch 메타 테이블 란?
     * ========================================================================
     * Spring Batch가 실행 이력을 저장하는 DB 테이블
     * 
     * 주요 테이블:
     * - BATCH_JOB_INSTANCE: Job 실행 단위 (파라미터별)
     * - BATCH_JOB_EXECUTION: Job 실행 기록 (매 실행마다)
     * - BATCH_JOB_EXECUTION_PARAMS: Job 실행 파라미터
     * - BATCH_STEP_INSTANCE: Step 실행 단위
     * - BATCH_STEP_EXECUTION: Step 실행 기록
     * - BATCH_JOB_EXECUTION_CONTEXT: Job 실행 컨텍스트
     * - BATCH_STEP_EXECUTION_CONTEXT: Step 실행 컨텍스트
     * 
     * 테이블 관계:
     * BATCH_JOB_INSTANCE (1) -- (N) BATCH_JOB_EXECUTION
     * BATCH_JOB_EXECUTION (1) -- (N) BATCH_STEP_EXECUTION
     * 
     * 활용:
     * - 실행 이력 조회
     * - 실패 원인 분석
     * - 성능 모니터링
     * - 재실행 판단
     * ========================================================================
     */

    private int executionCount = 0;

    // ===== Tasklet: 메타 테이블 정보 출력 =====

    @Bean
    public Step tutorial14_metaTableStep(JobRepository jobRepository,
                                          PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial14_metaTableStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                executionCount++;
                
                System.out.println("=".repeat(70));
                System.out.println("[Tutorial14] Spring Batch 메타 테이블 활용");
                System.out.println("=".repeat(70));
                
                var stepExecution = chunkContext.getStepContext().getStepExecution();
                var jobExecution = stepExecution.getJobExecution();
                
                System.out.println("현재 Job 정보:");
                System.out.println("  - Job Name: " + jobExecution.getJobInstance().getJobName());
                System.out.println("  - Job Instance ID: " + jobExecution.getJobId());
                System.out.println("  - Job Execution ID: " + jobExecution.getId());
                System.out.println("  - Status: " + jobExecution.getStatus());
                System.out.println("  - Start Time: " + jobExecution.getStartTime());
                System.out.println("  - End Time: " + jobExecution.getEndTime());
                
                System.out.println("-".repeat(70));
                System.out.println("현재 Step 정보:");
                System.out.println("  - Step Name: " + stepExecution.getStepName());
                System.out.println("  - Step Execution ID: " + stepExecution.getId());
                System.out.println("  - Status: " + stepExecution.getStatus());
                System.out.println("  - Read Count: " + stepExecution.getReadCount());
                System.out.println("  - Write Count: " + stepExecution.getWriteCount());
                System.out.println("  - Commit Count: " + stepExecution.getCommitCount());
                System.out.println("  - Rollback Count: " + stepExecution.getRollbackCount());
                
                System.out.println("-".repeat(70));
                System.out.println("쿼리 예시 (직접 DB 접속 시 사용):");
                System.out.println("  -- Job 실행 이력 조회");
                System.out.println("  SELECT * FROM BATCH_JOB_EXECUTION ORDER BY START_TIME DESC LIMIT 10;");
                System.out.println("");
                System.out.println("  -- 실패한 Job 조회");
                System.out.println("  SELECT * FROM BATCH_JOB_EXECUTION WHERE STATUS = 'FAILED';");
                System.out.println("");
                System.out.println("  -- Step 통계 조회");
                System.out.println("  SELECT STEP_NAME, COUNT(*), SUM(READ_COUNT), SUM(WRITE_COUNT)");
                System.out.println("  FROM BATCH_STEP_EXECUTION GROUP BY STEP_NAME;");
                
                System.out.println("-".repeat(70));
                System.out.println("application.properties 설정:");
                System.out.println("  spring.batch.jdbc.initialize-schema=always");
                System.out.println("  # embedded (H2, 기본값)");
                System.out.println("  # always (항상 실행)");
                System.out.println("  # never (수동 관리)");
                
                System.out.println("=".repeat(70));
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    // ===== Job =====

    @Bean
    public Job tutorial14_metaTableJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial14_metaTableJob", jobRepository)
            .start(tutorial14_metaTableStep(jobRepository, null))
            .build();
    }

    /**
     * ========================================================================
     * 메타 테이블 스키마 요약
     * ========================================================================
     * 
     * BATCH_JOB_INSTANCE
     * - JOB_INSTANCE_ID (PK)
     * - VERSION
     * - JOB_NAME
     * - JOB_KEY
     * 
     * BATCH_JOB_EXECUTION
     * - JOB_EXECUTION_ID (PK)
     * - VERSION
     * - JOB_INSTANCE_ID (FK)
     * - STATUS
     * - CREATE_TIME
     * - START_TIME
     * - END_TIME
     * - EXIT_CODE
     * - EXIT_MESSAGE
     * - LAST_UPDATED
     * 
     * BATCH_STEP_EXECUTION
     * - STEP_EXECUTION_ID (PK)
     * - VERSION
     * - STEP_NAME
     * - JOB_EXECUTION_ID (FK)
     * - STATUS
     * - READ_COUNT
     * - WRITE_COUNT
     * - COMMIT_COUNT
     * - ROLLBACK_COUNT
     * - START_TIME
     * - END_TIME
     * - EXIT_CODE
     * 
     * BATCH_JOB_EXECUTION_PARAMS
     * - JOB_EXECUTION_ID (FK)
     * - KEY_NAME
     * - PARAMETER_TYPE
     * - PARAMETER_NAME
     * - PARAMETER_VALUE
     * - IDENTIFYING
     * ========================================================================
     */
}
