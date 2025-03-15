package faithcoderlab.tablebookingservice.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 enum 클래스
 * 애플리케이션에서 발생할 수 있는 다양한 예외 상황에 대한 에러 코드 정의
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 전화번호입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다."),
    STORE_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 매장 이름입니다."),

    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "예약 시간이 유효하지 않습니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 시간에 이미 예약이 존재합니다."),
    RESERVATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "예약할 수 없는 상태입니다."),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    NOT_REVIEW_OWNER(HttpStatus.FORBIDDEN, "리뷰 작성자만 수정할 수 있습니다."),
    INVALID_REVIEW_PERMISSION(HttpStatus.FORBIDDEN, "리뷰를 삭제할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
