package com.example.playground.jmeter.lock.dto;

import jakarta.validation.constraints.Min;

public record StockRequest(@Min(1) int amount) {
}
