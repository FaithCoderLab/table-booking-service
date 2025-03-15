package faithcoderlab.tablebookingservice.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 응답 래퍼 클래스
 * 일관된 응답 형식을 위한 공통 응답 객체
 *
 * @param <T> 응답 데이터 타입
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    /**
     * 성공 응답 생성 메서드
     *
     * @param message 성공 메시지
     * @param data    응답 데이터
     * @param <T>     응답 데이터 타입
     * @return ApiResponse 객체
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 실패 응답 생성 메서드
     *
     * @param message 실패 메시지
     * @param <T>     응답 데이터 타입
     * @return ApiResponse 객체
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null).build();
    }
}
