package faithcoderlab.tablebookingservice.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 관련 DTO 클래스
 * 알림 데이터 전송 객체
 */
public class NotificationDto {

    /**
     * 알림 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long notificationId;
        private Long userId;
        private String title;
        private String content;
        private String type;
        private Long referenceId;
        private boolean read;
        private LocalDateTime createdAt;
    }

    /**
     * 알림 목록 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationListResponse {
        private long totalUnread;
        private List<Response> notifications;
    }
}
