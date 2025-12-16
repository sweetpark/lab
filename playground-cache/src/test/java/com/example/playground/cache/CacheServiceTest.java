package com.example.playground.cache;


import com.example.playground.cache.source.CacheService;
import com.example.playground.cache.source.Repository;
import com.example.playground.cache.source.entity.Member;
import com.example.playground.cache.source.entity.Terms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(TestRepoConfig.class)
class CacheServiceTest {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private Repository repository;

    private final Member member = new Member("john", 30, "Seoul");
    private final Terms terms = new Terms("privacy", true, LocalDateTime.now(), LocalDateTime.now());

    @BeforeEach
    void setup() {
        // 캐시 비우기
        cacheManager.getCache("member").clear();
        cacheManager.getCache("terms").clear();
    }

    @Test
    void testMemberIsCached() {
        given(repository.getMember("john")).willReturn(member);

        // 메서드 call
        Member m1 = cacheService.getMember("john");
        assertThat(m1).isEqualTo(member);
        verify(repository, times(1)).getMember("john");

        // 캐시 hit
        Member m2 = cacheService.getMember("john");
        assertThat(m2).isEqualTo(member);
        verify(repository, times(1)).getMember("john"); // getMember 한번만 호출 (나머지 한번은 캐싱)
    }

    @Test
    void testTermsIsCached() {
        given(repository.getTerms("privacy")).willReturn(terms);

        Terms t1 = cacheService.getTerms("privacy");
        Terms t2 = cacheService.getTerms("privacy");

        assertThat(t1).isEqualTo(terms);
        assertThat(t2).isEqualTo(terms);
        verify(repository, times(1)).getTerms("privacy");
    }

    @Test
    void testUpdateMemberCachePut() {
        given(repository.getMember("john")).willReturn(member);

        Member updated = new Member("john", 35, "Busan");
        Member result = cacheService.updateMember(updated);

        assertThat(result.getAge()).isEqualTo(35);
        assertThat(result.getAddr()).isEqualTo("Busan");

        // 캐시에서도 확인
        Member cached = cacheManager.getCache("member").get("john", Member.class);
        assertThat(cached).isNotNull();
        assertThat(cached.getAge()).isEqualTo(35);
    }

    @Test
    void testRemoveMemberEvictsCache() {
        given(repository.getMember("john")).willReturn(member);
        cacheService.getMember("john"); // 캐시에 저장

        cacheService.removeMember("john");

        // 캐시에서 제거되었는지 확인
        assertThat(cacheManager.getCache("member").get("john")).isNull();
    }

    @Test
    void testClearAllMembersEvictsAll() {
        given(repository.getMember("john")).willReturn(member);
        given(repository.getMember("paul")).willReturn(new Member("paul", 28, "Daegu"));

        cacheService.getMember("john");
        cacheService.getMember("paul");

        cacheService.clearAllMembers();

        assertThat(cacheManager.getCache("member").get("john")).isNull();
        assertThat(cacheManager.getCache("member").get("paul")).isNull();
    }
}