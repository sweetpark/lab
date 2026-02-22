package com.example.playground.thread.service.db;

import com.example.playground.thread.entity.Product;
import com.example.playground.thread.mapper.OptimisticMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

/**
 * [낙관적 락 (Optimistic Lock)]
 * 데이터 충돌이 거의 없을 것이라고 낙관적으로 가정하고, 버전을 통해 정합성을 맞춤
 * DB 락을 잡지 않고 애플리케이션 레벨에서 처리합니다.
 *
 * ✔ 장점: DB 자원을 점유하지 않으므로 동시성 처리가 효율적임
 * ✔ 단점: 충돌 시 개발자가 직접 재시도(Retry) 로직을 작성해야 함
 */
@Service
@RequiredArgsConstructor
public class OptimisticService {

    private final OptimisticMapper mapper;
    private final OptimisticTxService txService;

    /**
     * [낙관적 락의 핵심: 재시도 로직]
     * 충돌 발생 시(OptimisticLockingFailureException) 일정 횟수만큼 다시 시도합니다.
     *
     * ✔ 주의사항:
     * 1. 충돌이 잦은 환경(Hot Spot)에서는 CPU 자원을 많이 소모하여 성능이 저하될 수 있음
     * 2. 백오프(Backoff) 전략을 활용하여 충돌 주기를 늦추는 것이 좋습니다.
     * 3. 재시도 횟수 제한(Max Retry)을 두어 무한 루프를 방지해야 함
     */
    public void decreaseWithRetry(Long id){

        int maxRetry = 5;

        for (int attempt = 0; attempt < maxRetry; attempt++) {
            try {
                // 개별 트랜잭션으로 처리하여 충돌 시 이 부분만 롤백됨
                txService.decreaseOnce(id);
                return;
            } catch (OptimisticLockingFailureException e) {
                // 마지막 재시도까지 실패 시 예외 던짐
                if (attempt == maxRetry - 1) {
                    throw new RuntimeException("낙관적 락 재시도 한계 도달 (충돌 과다)");
                }

                // 지수 백오프(Backoff)와 유사하게 대기 시간을 두어 충돌 가능성을 줄임
                try {
                    Thread.sleep(10L + (attempt * 10)); // 대기 시간 점진적 증가
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public Product getProduct(Long id){
        return mapper.selectById(id);
    }
}
