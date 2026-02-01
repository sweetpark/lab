package com.example.playground.파일InputOutput.stream;

import java.io.*;

public class BufferedIoMain {
    public static void main(String[] args) throws IOException {
        // 상황: 10MB짜리 파일을 복사해야 함.

        // 1. 기반 스트림 생성 (파일과 직접 연결되는 관)
        FileInputStream fis = new FileInputStream("playground-lang/src/main/java/com/example/playground/파일InputOutput/original.dat");
        FileOutputStream fos = new FileOutputStream("playground-lang/src/main/java/com/example/playground/파일InputOutput/copy.dat");

        // [문제 1] 성능 향상을 위해 '보조 스트림(버퍼)'을 끼웁니다.
        // 설명: 이 클래스를 감싸면 8192 byte(8KB) 사이즈의 버퍼를 내부적으로 가집니다.
        // 왜 사용함?: OS 시스템 콜(디스크 접근) 횟수를 획기적으로 줄여 속도를 수십~수백 배 빠르게 합니다.
        // 왜 중요한가?: 실무에서 파일 I/O 시 버퍼 없이는 성능 이슈로 사용이 불가능할 정도입니다.
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        int data;
        // 버퍼로부터 데이터를 읽어옵니다. (내부 버퍼가 비면 그때 디스크에서 왕창 읽어옴)
        while ((data = bis.read()) != -1) {
            bos.write(data);
        }

        // [문제 2] 사용이 끝난 스트림은 반드시 닫아야 합니다.
        // 설명: 보조 스트림만 닫으면, 연결된 기반 스트림(fis, fos)도 자동으로 닫힙니다.
        // 왜 중요한가?: 닫지 않으면 OS의 파일 핸들(File Descriptor) 자원이 누수되어, 나중에 파일을 못 열게 됩니다.
        bos.close();
        bis.close();
    }
}