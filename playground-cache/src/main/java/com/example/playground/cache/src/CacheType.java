package com.example.playground.cache.src;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CacheType {
    MEMBER("member", 30L, 1000L),
    TERMS("terms", 60L, 100L);

    private final String name;
    private final Long expiredAfterWrite;
    private final Long maximumSize;
}
