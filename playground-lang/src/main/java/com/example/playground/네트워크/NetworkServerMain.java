package com.example.playground.네트워크;

import java.io.*;
import java.net.*;

public class NetworkServerMain {

    public static void main(String[] args) {
        int port = 12345;

        // [문제 1] 서버 쪽에서 클라이언트의 연결 요청을 기다리는 객체는?
        // 설명: 문지기 역할을 합니다. 연결이 들어오면 실제 통신할 소켓을 만들어줍니다.
        // 왜 사용함?: 하나의 포트(12345)를 열어두고 다수의 클라이언트를 받기 위해서입니다.
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("서버 대기 중...");

            // [문제 2] 클라이언트가 접속할 때까지 블로킹(대기)하다가, 접속하면 통신용 소켓을 반환하는 메서드는?
            // 설명: "수락하다"라는 뜻의 메서드입니다.
            // 왜 중요한가?: 이 메서드가 호출되어야 비로소 클라이언트와 데이터를 주고받을 수 있는 '전화기(Socket)'가 생깁니다.
            Socket socket = serverSocket.accept( );

            System.out.println("클라이언트 접속 성공!");

            // [문제 3] 자바 7부터 도입된, close()를 자동으로 호출해주는 문법 구조는?
            // 설명: try(...) 괄호 안에 자원 객체를 선언하면, try 블록이 끝나거나 예외가 터져도 자동으로 close() 됩니다.
            // 왜 중요한가?: 서버가 24시간 돌아갈 때 close()를 실수로 누락하면 '리소스 누수(Leak)'로 서버가 뻗어버립니다.
            // 이걸 쓰면 finally 블록 지옥에서 탈출할 수 있습니다.
            try (
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())
            ) {
                // 클라이언트가 보낸 메시지 읽기
                String msg = input.readUTF();
                System.out.println("받은 메시지: " + msg);

                // 클라이언트에게 메시지 보내기
                output.writeUTF("Hello Client!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}