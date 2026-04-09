package com.example.playground.batch.source.tutorial.chunk;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class Tutorial06_ChunkProcessor {

    /**
     * ========================================================================
     * Chunk Processing Components 란?
     * ========================================================================
     * Chunk 기반 처리 3대 핵심 컴포넌트:
     * 
     * 1. ItemReader
     *    - 데이터 소스로부터 아이템 읽기
     *    - null 반환 시 읽기 종료
     *    - 예: JdbcCursorItemReader, JpaPagingItemReader, FlatFileItemReader
     * 
     * 2. ItemProcessor (선택적)
     *    - 각 아이템 변환/필터링
     *    - null 반환 시 해당 아이템 건너뛰기
     *    - 예: DomainClassConverter, PassThroughItemProcessor
     * 
     * 3. ItemWriter
     *    - 아이템 목록 일괄 기록
     *    - Chunk Size만큼 모아서 한 번에 기록
     *    - 예: JdbcBatchItemWriter, JpaItemWriter, FlatFileItemWriter
     * ========================================================================
     */

    // ===== 데이터 모델 =====

    public record User(Long id, String name, String email, boolean active) {}

    // ===== Reader =====

    @Bean
    public ItemReader<User> tutorial06_userReader() {
        User[] users = {
            new User(1L, "홍길동", "hong@example.com", true),
            new User(2L, "김철수", "kim@example.com", true),
            new User(3L, "이영희", "lee@example.com", false),
            new User(4L, "박지민", "park@example.com", true),
            new User(5L, "정수민", "jung@example.com", false),
        };
        int[] index = {0};
        return () -> {
            if (index[0] >= users.length) return null;
            return users[index[0]++];
        };
    }

    // ===== Processor =====

    @Bean
    public ItemProcessor<User, String> tutorial06_userProcessor() {
        return user -> {
            System.out.println("[Processor] 사용자 처리: " + user.id() + " - " + user.name());
            
            // 비활성 사용자는 건너뛰기
            if (!user.active()) {
                System.out.println("  -> 비활성 사용자 건너뜀");
                return null;
            }
            
            // 이메일 마스킹
            String maskedEmail = user.email().replaceAll("(?<=.{3}).(?=.*@)", "*");
            return user.id() + "," + user.name() + "," + maskedEmail;
        };
    }

    // ===== Writer =====

    @Bean
    public ItemWriter<String> tutorial06_userWriter() {
        return items -> {
            System.out.println("[Writer] " + items.size() + "건 기록");
            System.out.println("-".repeat(40));
            items.forEach(item -> System.out.println("  " + item));
            System.out.println("-".repeat(40));
        };
    }

    // ===== Step =====

    @Bean
    public Step tutorial06_processorStep(JobRepository jobRepository,
                                           PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial06_processorStep", jobRepository)
            .<User, String>chunk(2)
            .reader(tutorial06_userReader())
            .processor(tutorial06_userProcessor())
            .writer(tutorial06_userWriter())
            .build();
    }

    // ===== Job =====

    @Bean
    public Job tutorial06_processorJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial06_processorJob", jobRepository)
            .start(tutorial06_processorStep(jobRepository, null))
            .build();
    }
}
