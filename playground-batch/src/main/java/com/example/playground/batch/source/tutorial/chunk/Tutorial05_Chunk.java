package com.example.playground.batch.source.tutorial.chunk;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
public class Tutorial05_Chunk {

    /**
     * ========================================================================
     * Chunk 란?
     * ========================================================================
     * 대량 데이터 처리를 위한 반복 패턴
     * - Chunk Size: 한 트랜잭션에서 처리하는 아이템 수
     * - 처리 흐름: Reader -> Processor -> Writer
     * 
     * Chunk 처리 과정:
     * 1. Reader가 Chunk Size만큼 데이터 읽기
     * 2. Processor가 각 아이템 변환 (선택적)
     * 3. Writer가 한 번에 배치 기록
     * 4. 트랜잭션 커밋
     * 5. 반복until 모든 데이터 처리 완료
     * 
     * 예시 (Chunk Size = 10, 총 25건):
     *   Chunk 1: 10건 읽기 -> 처리 -> 기록 (트랜잭션1)
     *   Chunk 2: 10건 읽기 -> 처리 -> 기록 (트랜잭션2)
     *   Chunk 3: 5건 읽기 -> 처리 -> 기록 (트랜잭션3)
     * 
     * 장점:
     * - 메모리 효율적 (한 번에 Chunk Size만큼만 로드)
     * - 트랜잭션 단위 처리 (실패 시 Chunk 단위 롤백)
     * - 병렬 처리 가능
     * ========================================================================
     */

    @Bean
    public ItemReader<Integer> tutorial05_reader() {
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        return new org.springframework.batch.item.support.ListItemReader<>(data);
    }

    @Bean
    public ItemProcessor<Integer, String> tutorial05_processor() {
        return item -> {
            System.out.println("[Read] " + item);
            return "Item-" + item;
        };
    }

    @Bean
    public Step tutorial05_chunkStep(JobRepository jobRepository,
                                     PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial05_chunkStep", jobRepository)
            .<Integer, String>chunk(3)
            .reader(tutorial05_reader())
            .processor(tutorial05_processor())
            .writer(items -> {
                System.out.println("[Write] Chunk Size: " + items.size());
                items.forEach(item -> System.out.println("  -> " + item));
            })
            .build();
    }

    @Bean
    public Job tutorial05_chunkJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial05_chunkJob", jobRepository)
            .start(tutorial05_chunkStep(jobRepository, null))
            .build();
    }
}
