package com.example.playground.파일InputOutput.인코딩;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TextEncodingMain {
    public static void main(String[] args) throws IOException {

        // 상황: 바이트로 된 파일 데이터를 읽어서, 자바의 String(문자)으로 변환하고 싶음.
        FileInputStream fis = new FileInputStream("playground-lang/src/main/java/com/example/playground/인코딩/text.txt");

        // [문제 1] 바이트 스트림(FIS)을 문자 스트림(Reader)으로 변환해주는 '보조 스트림'은?
        // 설명: "입력 스트림을 읽는 놈(Reader)"이라는 뜻의 이름입니다.
        // 왜 사용함?: byte 데이터를 지정된 문자셋(Charset)에 맞춰 char로 해석(디코딩)해줍니다.
        // 왜 중요한가?: 한글은 3byte(UTF-8 기준)인데, 1byte씩 읽으면 다 깨집니다. 문자로 온전히 읽으려면 필수입니다.
        Reader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);

        // [문제 2] 문자를 읽을 때도 성능을 위해 버퍼를 씁니다. 특히 '한 줄씩 읽기' 기능이 강력합니다.
        // 설명: Reader 계열의 버퍼 보조 스트림입니다.
        // 왜 사용함?: readLine() 메서드를 제공하여, 엔터(\n) 단위로 편하게 String을 가져올 수 있습니다.
        BufferedReader br = new BufferedReader(reader);

        String line;
        while ((line = br.readLine()) != null) { // [문제 3] 한 줄씩 읽는 메서드
            System.out.println(line);
        }

        br.close();
    }
}