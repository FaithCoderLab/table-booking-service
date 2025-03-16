package faithcoderlab.tablebookingservice.domain.notification.service;

import faithcoderlab.tablebookingservice.domain.notification.dto.NotificationDto;
import faithcoderlab.tablebookingservice.domain.notification.entity.Notification;
import faithcoderlab.tablebookingservice.domain.notification.repository.NotificationRepository;
import faithcoderlab.tablebookingservice.domain.user.entity.User;
import faithcoderlab.tablebookingservice.domain.user.repository.UserRepository;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 알림 서비스 클래스
 * 알림 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 예약 상태 변경 알림 생성
     *
     * @param userId          사용자 ID
     * @param reservationId   예약 ID
     * @param storeName       매장 이름
     * @param approved        승인 여부
     * @param message         메시지
     * @param rejectionReason 거절 이유 (거절 시에만 사용)
     * @return 생성된 알림 정보
     */
    @Transactional
    public NotificationDto.Response createReservationStatusNotification(
            Long userId, Long reservationId, String storeName,
            boolean approved, String message, String rejectionReason
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String title = approved
                ? "예약이 승인되었습니다"
                : "예약이 거절되었습니다";

        String content = approved
                ? String.format("%s 매장의 예약이 승인되었습니다. %s", storeName, message)
                : String.format("%s 매장의 예약이 거절되었습니다. %s", storeName, rejectionReason);

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .type("RESERVATION_STATUS")
                .referenceId(reservationId)
                .read(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        return convertToResponseDto(savedNotification);
    }

    /**
     * 사용자 알림 목록 조회
     *
     * @param userId 사용자 ID
     * @param size   페이지 크기
     * @return 알림 목록
     */
    @Transactional(readOnly = true)
    public NotificationDto.NotificationListResponse getUserNotifications(Long userId, int size) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(0, size);
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        long totalUnread = notificationRepository.countByUserIdAndReadFalse(userId);

        List<NotificationDto.Response> notificationResponses = notifications.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return NotificationDto.NotificationListResponse.builder()
                .totalUnread(totalUnread)
                .notifications(notificationResponses)
                .build();
    }

    /**
     * 알림 읽음 처리
     *
     * @param userId 사용자 ID
     * @param notificationId 알림 ID
     * @return 읽음 처리된 알림 정보
     */
    @Transactional
    public NotificationDto.Response markNotificationAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST, "알림을 찾을 수 없습니다."));

        if (!notification.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return convertToResponseDto(updatedNotification);
    }

    /**
     * Notification 엔티티를 Response DTO로 변환
     *
     * @param notification Notification 엔티티
     * @return Response DTO
     */
    private NotificationDto.Response convertToResponseDto(Notification notification) {
        return NotificationDto.Response.builder()
                .notificationId(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .referenceId(notification.getReferenceId())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
