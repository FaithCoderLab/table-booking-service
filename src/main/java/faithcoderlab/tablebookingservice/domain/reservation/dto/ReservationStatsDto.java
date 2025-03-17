package faithcoderlab.tablebookingservice.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 예약 통계 관련 DTO 클래스
 * 매장별 예약 통계 데이터를 담는 객체
 */
public class ReservationStatsDto {

    /**
     * 기간별 예약 통계 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodStatsResponse {
        private Long storeId;
        private String storeName;
        private LocalDate startDate;
        private LocalDate endDate;
        private long totalReservations;
        private long confirmedReservations;
        private long cancelledReservations;
        private long noShowReservations;
        private double averagePartySize;
        private Map<String, Long> dailyReservationCounts;
    }

    /**
     * 시간대별 예약 통계 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotStatsResponse {
        private Long storeId;
        private String storeName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Map<String, Long> timeSlotDistribution;
        private List<TimeSlotData> mostPopularTimeSlots;
    }

    /**
     * 시간대 데이터 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotData {
        private LocalTime timeSlot;
        private Long count;
    }

    /**
     * 상태별 예약 통계 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusStatsResponse {
        private Long storeId;
        private String storeName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Map<String, Long> statusDistribution;
        private double confirmationRate;
        private double cancellationRate;
        private double noShowRate;
    }

    /**
     * 통계 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsRequest {
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
