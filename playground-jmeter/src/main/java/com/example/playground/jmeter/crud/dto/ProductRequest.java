package com.example.playground.jmeter.crud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Java 16+ record: 불변 DTO. getter/equals/hashCode/toString을 자동 생성한다.
// @Valid와 함께 사용하면 컨트롤러 진입 전 Bean Validation이 자동으로 실행된다.
public record ProductRequest(
        @NotBlank String name,
        @NotNull @Positive Integer price,
        @NotNull @Positive Integer stock
) {
}
