package com.example.playground.batch.source.tutorial.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableScheduling
public class Tutorial10_Scheduler {

    /**
     * ========================================================================
     * Scheduler 란?
     * ========================================================================
     * 배치 잡의 자동 실행을 위한 스케줄링
     * 
     * Spring Batch에서 스케줄링 방법:
     * 1. @Scheduled 어노테이션 (Spring 내장)
     * 2. Quartz Scheduler (외부 의존성)
     * 
     * @Scheduled 옵션:
     * - fixedDelay: 이전 작업 완료 후 n초 후 재실행
     * - fixedRate: 시작 시간 기준 n초마다 실행
     * - cron: Cron 표현식 사용
     * 
     * 주의사항:
     * - @EnableScheduling 필요
     * - Cluster 환경에서는 중복 실행 방지 필요
     * - 긴 실행 시간의 Job은 fixedRate보다 fixedDelay 권장
     * ========================================================================
     */

    private int executionCount = 0;

    // ===== Tasklet =====

    @Bean
    public Tasklet tutorial10_scheduledTasklet() {
        return (contribution, chunkContext) -> {
            executionCount++;
            System.out.println("=".repeat(60));
            System.out.println("[Scheduler] Scheduled Job 실행");
            System.out.println("=".repeat(60));
            System.out.println("실행 횟수: " + executionCount);
            System.out.println("실행 시각: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("Job Name: tutorial10_scheduledJob");
            System.out.println("=".repeat(60));
            return RepeatStatus.FINISHED;
        };
    }

    // ===== Step =====

    @Bean
    public Step tutorial10_scheduledStep(JobRepository jobRepository,
                                          PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial10_scheduledStep", jobRepository)
            .tasklet(tutorial10_scheduledTasklet(), platformTransactionManager)
            .build();
    }

    // ===== Job =====

    @Bean
    public Job tutorial10_scheduledJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial10_scheduledJob", jobRepository)
            .start(tutorial10_scheduledStep(jobRepository, null))
            .build();
    }

    /**
     * ===== 스케줄링 활성화 가이드 =====
     * 
     * 1. application.properties에서 자동 Job 실행 비활성화:
     *    spring.batch.job.enabled=false
     * 
     * 2. @Scheduled 어노테이션 주석 해제 후 사용:
     *    
     *    @Autowired
     *    private JobLauncher jobLauncher;
     *    
     *    @Scheduled(cron = "0 * * * * *")  // 매분 0초에 실행
     *    public void runJobWithCron() {
     *        try {
     *            JobParameters params = new JobParametersBuilder()
     *                .addLong("timestamp", System.currentTimeMillis())
     *                .toJobParameters();
     *            jobLauncher.run(tutorial10_scheduledJob(null), params);
     *        } catch (Exception e) {
     *            System.err.println("Job 실행 실패: " + e.getMessage());
     *        }
     *    }
     */
}
