package faithcoderlab.tablebookingservice.global.exception;

import lombok.Getter;

/**
 * 사용자 정의 예외 클래스
 * 애플리케이션에서 발생하는 예외를 처리하기 위한 사용자 정의 예외
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 에러 코드를 받아 예외 생성
     *
     * @param errorCode 에러 코드
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드와 메시지를 받아 예외 생성
     *
     * @param errorCode 에러 코드
     * @param message   에러 메시지
     */
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
