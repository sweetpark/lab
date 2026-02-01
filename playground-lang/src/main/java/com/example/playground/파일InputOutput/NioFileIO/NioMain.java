package com.example.playground.파일InputOutput.NioFileIO;

import java.io.IOException;
import java.nio.file.*;

public class NioMain {
    public static void main(String[] args) throws IOException {

        // [문제 1] 과거의 File 클래스 대신, 파일의 '위치'를 나타내는 최신 인터페이스는?
        // 설명: 파일 시스템의 경로를 추상화한 객체입니다. (Paths.get() 또는 Path.of()로 생성)
        // 왜 사용함?: OS(윈도우/맥/리눅스)마다 다른 경로 구분자 등을 알아서 처리해줍니다.
        Path source = Path.of("playground-lang/src/main/java/com/example/playground/파일InputOutput/NioFileIO/input.txt");
        Path target = Path.of("playground-lang/src/main/java/com/example/playground/파일InputOutput/NioFileIO/output.txt");

        // [문제 2] 스트림을 열고, 읽고, 쓰고, 닫는 복잡한 코드를 '단 한 줄'로 끝내는 유틸리티 클래스는?
        // 설명: 파일 조작(복사, 이동, 삭제 등)을 위한 static 메서드 모음집입니다.
        // 왜 사용함?: 내부적으로 최적화된 OS 시스템 콜을 사용하여 성능이 매우 빠르고 코드가 간결해집니다.
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("파일 복사 완료");

        // [심화] 파일 내용을 문자열로 한 번에 다 읽어오는 메서드도 있습니다. (작은 파일용)
        // String content = ______.readString(source);
    }
}