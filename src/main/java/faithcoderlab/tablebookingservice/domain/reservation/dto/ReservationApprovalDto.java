package faithcoderlab.tablebookingservice.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 예약 승인/거절 관련 DTO 클래스
 */
public class ReservationApprovalDto {

    /**
     * 예약 승인/거절 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalRequest {
        @NotNull(message = "승인 여부는 필수 입력 항목입니다.")
        private Boolean approved;

        private String rejectionReason;
    }

    /**
     * 예약 승인/거절 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalResponse {
        private Long reservationId;
        private String storeName;
        private String userName;
        private Boolean approved;
        private String message;
        private String rejectionReason;
    }
}
