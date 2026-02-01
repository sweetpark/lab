package com.example.playground.HTTP서버;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// [서버 메인 클래스]
public class MiniWasMain {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        // 1. URL과 실제 컨트롤러 객체를 저장할 맵
        Map<String, Object> controllerMap = new ConcurrentHashMap<>();
        controllerMap.put("/site1", new SiteController()); // /site1 요청 -> SiteController

        // 2. 서버 실행
        HttpServer server = new HttpServer(port, controllerMap);
        server.start();
    }
}

// [핵심 1] 요청을 받아서 처리하는 서버
class HttpServer {
    private final int port;
    private final Map<String, Object> controllerMap;

    public HttpServer(int port, Map<String, Object> controllerMap) {
        this.port = port;
        this.controllerMap = controllerMap;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("서버 시작! 포트: " + port);

            while (true) {
                // [문제 1] 1단계에서 배운 것. 클라이언트 연결을 기다리고 소켓을 받습니다.
                Socket socket = serverSocket.accept();

                // 요청 처리는 오래 걸릴 수 있으니 별도 스레드에서 처리합니다.
                new Thread(() -> handleRequest(socket)).start();
            }
        }
    }

    private void handleRequest(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // 1. 요청 라인 읽기 (예: "GET /site1 HTTP/1.1")
            String requestLine = reader.readLine();
            if (requestLine == null) return;

            // URL 추출 ("/site1")
            String[] parts = requestLine.split(" ");
            String url = parts[1];
            System.out.println("요청 URL: " + url);

            // [핵심 2] 리플렉션으로 메서드 찾아서 실행하기
            // controllerMap에서 해당 URL을 처리할 객체를 꺼냅니다.
            Object controller = controllerMap.get(url);

            if (controller != null) {
                Class<?> clazz = controller.getClass();
                // "site1" 메서드를 찾아서 실행 (단순화를 위해 URL과 메서드명이 같다고 가정)
                // 실제로는 @Mapping을 뒤져서 찾아야 하지만, 여기선 2단계 복습으로 대체합니다.
                String methodName = url.substring(1); // "/site1" -> "site1"

                // [문제 2] 2단계에서 배운 것. 이름으로 메서드 정보를 찾습니다.
                Method method = clazz.getMethod( methodName );

                // [문제 3] 2단계에서 배운 것. 메서드를 실행합니다.
                method.invoke( controller );

                // 응답 보내기 (HTTP 규격)
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/html; charset=UTF-8");
                writer.println();
                writer.println("<h1>" + url + " 페이지입니다</h1>");

            } else {
                writer.println("HTTP/1.1 404 Not Found");
                writer.println();
                writer.println("<h1>404 페이지를 찾을 수 없습니다</h1>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// [테스트용 컨트롤러]
class SiteController {
    public void site1() {
        System.out.println("== Site1 컨트롤러 실행 ==");
    }
}