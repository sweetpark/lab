package com.example.playground.batch.source.tutorial.advanced;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
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
public class Tutorial11_SkipAndRetry {

    /**
     * ========================================================================
     * Skip & Retry 란?
     * ========================================================================
     * 
     * Skip: 특정 예외 발생 시 해당 아이템 건너뛰기
     * - .skipLimit(횟수): 최대 건너뛸 수
     * - .skip(예외클래스): 건너뛸 예외 지정
     * 
     * Retry: 특정 예외 발생 시 재시도
     * - .retryLimit(횟수): 최대 재시도 횟수
     * - .retry(예외클래스): 재시도할 예외 지정
     * - .backoff(밀리초): 재시도 간격
     * 
     * SkipListener:
     * - onSkipInRead(): Reader에서 건너뛴 경우
     * - onSkipInWrite(): Writer에서 건너뛴 경우
     * - onSkipInProcess(): Processor에서 건너뛴 경우
     * 
     * 주의: Retry는 Chunk 내부에서만 동작
     * ========================================================================
     */

    // ===== 데이터 모델 =====

    public record Product(Long id, String name, double price) {}

    // ===== Reader (특정 아이템에서 예외 발생) =====

    @Bean
    public ItemReader<Product> tutorial11_productReader() {
        Product[] products = {
            new Product(1L, "노트북", 1200000),
            new Product(2L, "키보드", 150000),
            new Product(3L, "마우스", 50000),
            new Product(4L, "모니터", 400000),
            new Product(5L, "웹캠", 80000),
            new Product(6L, "헤드셋", 200000),
            new Product(7L, "USB허브", 30000),
            new Product(8L, "태블릿", 600000),
        };
        int[] index = {0};
        return () -> {
            if (index[0] >= products.length) return null;
            Product product = products[index[0]++];
            // ID 4번과 7번에서 예외 발생 시뮬레이션
            if (product.id() == 4 || product.id() == 7) {
                throw new RuntimeException("잠깐의 네트워크 문제!");
            }
            return product;
        };
    }

    // ===== Processor =====

    @Bean
    public ItemProcessor<Product, String> tutorial11_productProcessor() {
        return product -> {
            System.out.println("[Process] " + product.id() + " - " + product.name());
            return product.id() + ":" + product.name() + ":" + product.price();
        };
    }

    // ===== Writer =====

    @Bean
    public ItemWriter<String> tutorial11_productWriter() {
        return items -> System.out.println("[Write] " + items.size() + "건: " + items);
    }

    // ===== Skip Listener =====

    @Bean
    public SkipListener<Product, String> tutorial11_skipListener() {
        return new SkipListener<>() {
            @Override
            public void onSkipInRead(Throwable t) {
                System.out.println("[SkipListener] Read에서 건너뜀: " + t.getMessage());
            }

            @Override
            public void onSkipInWrite(String item, Throwable t) {
                System.out.println("[SkipListener] Write에서 건너뜀: " + item);
            }

            @Override
            public void onSkipInProcess(Product item, Throwable t) {
                System.out.println("[SkipListener] Process에서 건너뜀: " + item + " - " + t.getMessage());
            }
        };
    }

    // ===== Step (Skip 설정) =====

    @Bean
    public Step tutorial11_skipStep(JobRepository jobRepository,
                                     PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("tutorial11_skipStep", jobRepository)
            .<Product, String>chunk(3)
            .reader(tutorial11_productReader())
            .processor(tutorial11_productProcessor())
            .writer(tutorial11_productWriter())
            .listener(tutorial11_skipListener())
            .faultTolerant()
            .skipLimit(10)
            .skip(RuntimeException.class)
            .noSkip(IllegalArgumentException.class)
            .build();
    }

    // ===== Job =====

    @Bean
    public Job tutorial11_skipJob(JobRepository jobRepository) {
        return new JobBuilder("tutorial11_skipJob", jobRepository)
            .start(tutorial11_skipStep(jobRepository, null))
            .build();
    }
}
