package com.example.playground.lang.날짜;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Period;

/*
 * [주제: 자바 날짜와 시간 API (LocalDate, LocalDateTime, Period)]
 * * 1. 이 주제를 연습하는 이유:
 * - 과거의 Date, Calendar는 '불변'이 아니어서 값이 예기치 않게 바뀔 수 있고, 설계가 복잡합니다.
 * - 현대 자바는 JSR-310(java.time) 표준을 사용하여 안전하고 직관적으로 시간을 다룹니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - '불변성(Immutable)': 모든 계산 메서드가 새로운 객체를 반환하므로 사이드 이펙트가 없습니다.
 * - 직관적 메서드: plusDays, minusMonths 등 사람이 읽기 쉬운 API를 제공합니다.
 * * 3. 어떤 경우에 사용되는가?:
 * - 결제 후 7일 이내 환불 가능 여부 계산, 가입일로부터 오늘까지의 기간 계산, 로그 출력 시간 포맷팅 등.
 */

public class DateTimeTestMain {
    public static void main(String[] args) {

        // TODO 1: 2024년 1월 1일과 2024년 12월 31일 사이의 "개월 수"와 "일수" 차이를 구하세요.
        // 힌트: Period.between(start, end)를 사용하세요.
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Period period = Period.between(startDate, endDate);
        System.out.println("[TODO 1] : " + period.getMonths() + "개월 " + period.getDays() + "일");

        // 여기에 작성

        // TODO 2: 오늘 날짜로부터 1년 2개월 3일 후의 날짜를 계산하여 출력하세요.
        // 힌트: LocalDate는 불변이므로 plus 메서드 호출 후 결과를 새 변수에 담아야 합니다.
        LocalDate now = LocalDate.now();
        LocalDate plusDate = now.plusYears(1L).plusMonths(2L).plusDays(3L);
        System.out.println("[TODO 2] : " + plusDate);

        // 여기에 작성 (LocalDate futureDate = ...)

        // TODO 3: 현재 날짜와 시간(LocalDateTime)을 "yyyy-MM-dd HH:mm:ss" 형식으로 포맷팅하세요.
        // 힌트: DateTimeFormatter.ofPattern(...)을 사용하세요.
        LocalDateTime nowDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter simpleFormat = DateTimeFormatter.ofPattern("YYMMDDHHmmss");
        System.out.println("[TODO 3] : " + nowDateTime.format(formatter));
        System.out.println("[TODO 3] : " + nowDateTime.format(simpleFormat));



    }
}