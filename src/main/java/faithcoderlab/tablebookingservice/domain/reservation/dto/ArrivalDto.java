package faithcoderlab.tablebookingservice.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 도착 확인 관련 DTO 클래스
 */
public class ArrivalDto {
    /**
     * 도착 확인 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArrivalRequest {
        @NotNull(message = "예약 ID는 필수 입력 항목입니다.")
        private Long reservationId;

        @NotNull(message = "키오스크 ID는 필수 입력 항목입니다.")
        private String kioskId;
    }

    /**
     * 도착 확인 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArrivalResponse {
        private Long reservationId;
        private String userName;
        private String storeName;
        private LocalDateTime arrivedAt;
        private String message;
    }
}
