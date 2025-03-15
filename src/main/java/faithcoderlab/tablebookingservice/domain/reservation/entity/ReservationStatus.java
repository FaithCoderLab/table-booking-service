package faithcoderlab.tablebookingservice.domain.reservation.entity;

/**
 * 예약 상태 열거형
 * 예약의 다양한 상태를 정의
 */
public enum ReservationStatus {
    /**
     * 대기 중 - 예약 요청이 제출되었지만 아직 승인되지 않음
     */
    PENDING,

    /**
     * 승인됨 - 예약이 점장에 의해 승인됨
     */
    CONFIRMED,

    /**
     * 도착함 - 고객이 예약 시간에 도착하여 체크인함
     */
    ARRIVED,

    /**
     * 완료됨 - 예약 서비스가 완료됨
     */
    COMPLETED,

    /**
     * 취소됨 - 고객 또는 점장에 의해 예약이 취소됨
     */
    CANCELLED,

    /**
     * 거절됨 - 예약 요청이 점장에 의해 거절됨
     */
    REJECTED,

    /**
     * 노쇼 - 고객이 예약 시간에 나타나지 않음
     */
    NO_SHOW
}
