package faithcoderlab.tablebookingservice.domain.reservation.dto;

import faithcoderlab.tablebookingservice.domain.reservation.entity.ReservationStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 예약 관련 DTO 클래스
 * 예약 데이터 전송 객체
 */
public class ReservationDto {

    /**
     * 예약 가능 시간 조회 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableTimesRequest {
        @NotNull(message = "매장 ID는 필수 입력 항목입니다.")
        private Long storeId;

        @NotNull(message = "예약 날짜는 필수 입력 항목입니다.")
        @FutureOrPresent(message = "예약 날짜는 현재 또는 미래 날짜여야 합니다.")
        private LocalDate date;

        @Min(value = 1, message = "인원수는 최소 1명 이상이어야 합니다.")
        @Max(value = 20, message = "인원수는 최대 20명까지 가능합니다.")
        private Integer partySize;
    }

    /**
     * 예약 가능 시간 조회 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableTimesResponse {
        private Long storeId;
        private String storeName;
        private LocalDate date;
        private List<LocalTime> availableTimes;
    }

    /**
     * 예약 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "매장 ID는 필수 입력 항목입니다.")
        private Long storeId;

        @NotNull(message = "예약 날짜는 필수 입력 항목입니다.")
        @FutureOrPresent(message = "예약 날짜는 현재 또는 미래 날짜여야 합니다.")
        private LocalDate reservationDate;

        @NotNull(message = "예약 시간은 필수 입력 항목입니다.")
        private LocalTime reservationTime;

        @NotNull(message = "인원수는 필수 입력 항목입니다.")
        @Min(value = 1, message = "인원수는 최소 1명 이상이어야 합니다.")
        @Max(value = 20, message = "인원수는 최대 20명까지 가능합니다.")
        private Integer partySize;

        private String specialRequests;
    }

    /**
     * 예약 생성 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResponse {
        private Long reservationId;
        private Long storeId;
        private String storeName;
        private Long userId;
        private String userName;
        private LocalDate reservationDate;
        private LocalTime reservationTime;
        private Integer partySize;
        private ReservationStatus status;
        private String specialRequests;
        private LocalDateTime createdAt;
    }

    /**
     * 예약 정보 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationInfoResponse {
        private Long reservationId;
        private Long storeId;
        private String storeName;
        private Long userId;
        private String userName;
        private String userPhone;
        private LocalDate reservationDate;
        private LocalTime reservationTime;
        private Integer partySize;
        private ReservationStatus status;
        private LocalDateTime arrivedAt;
        private LocalDateTime completedAt;
        private String specialRequests;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
