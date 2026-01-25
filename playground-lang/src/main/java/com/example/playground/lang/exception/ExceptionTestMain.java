package com.example.playground.lang.exception;

/*
 * [주제: 자바 예외 처리 (Exception Handling)]
 * * 1. 이 주제를 연습하는 이유:
 * - 프로그램 실행 중 발생할 수 있는 예기치 못한 오류(네트워크 단절, 파일 없음 등)를 안전하게 처리하여 프로그램의 비정상 종료를 막기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 정상 흐름과 예외 흐름을 분리하여 코드 가독성을 높입니다.
 * - 예외가 발생했을 때 적절한 복구 로직을 수행하거나, 사용자에게 친절한 안내를 제공할 수 있습니다.
 * * 3. 어떤 경우에 사용되는가?:
 * - 외부 서버와의 통신 실패, DB 조회 결과 없음, 잘못된 사용자 입력값 검증 등 모든 에러 상황.
 */

// 커스텀 체크 예외 (반드시 처리하거나 던져야 함)
class NetworkClientException extends Exception {
    public NetworkClientException(String message) {
        super(message);
    }
}

class NetworkService {
    public void sendMessage(String data) throws NetworkClientException {
        if (data.contains("error")) {
            // TODO 1: "네트워크 연결 실패" 메시지를 담은 NetworkClientException을 발생(throw)시키세요.
            throw new NetworkClientException("네트워크 연결 실패");
        }
        System.out.println("데이터 전송 성공: " + data);
    }
}

public class ExceptionTestMain {
    public static void main(String[] args) {
        NetworkService service = new NetworkService();

        // TODO 2: try-catch-finally 구문을 사용하여 예외를 처리하세요.
        // 1. service.sendMessage("error data")를 호출합니다.
        // 2. catch 블록에서 에러 메시지를 출력합니다.
        // 3. finally 블록에서 "네트워크 자원 해제(정상/예외 모두 실행)"를 출력합니다.
        try{
            service.sendMessage("error data");
        }catch(NetworkClientException e){
            System.out.println(e.getMessage());
        }finally {
            System.out.println("네트워크 자원 해제 (정상/예외 모두 실행)");
        }

        System.out.println("--- 프로그램 정상 종료 ---");
    }
}