package faithcoderlab.tablebookingservice.domain.notification.controller;

import faithcoderlab.tablebookingservice.domain.notification.dto.NotificationDto;
import faithcoderlab.tablebookingservice.domain.notification.service.NotificationService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import faithcoderlab.tablebookingservice.global.security.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 알림 컨트롤러 클래스
 * 알림 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthenticationUtil authenticationUtil;

    /**
     * 사용자 알림 목록 조회 API
     *
     * @param size 조회할 알림 개수
     * @return 알림 목록 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<NotificationDto.NotificationListResponse>> getUserNotifications(
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = authenticationUtil.getCurrentUserId();
        NotificationDto.NotificationListResponse response = notificationService.getUserNotifications(userId, size);

        return ResponseEntity.ok(ApiResponse.success("알림 목록을 성공적으로 조회했습니다.", response));
    }

    /**
     * 알림 읽음 처리 API
     *
     * @param notificationId 알림 ID
     * @return 읽음 처리 결과 응답
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationDto.Response>> markNotificationAsRead(
            @PathVariable Long notificationId
    ) {
        Long userId = authenticationUtil.getCurrentUserId();
        NotificationDto.Response response = notificationService.markNotificationAsRead(userId, notificationId);

        return ResponseEntity.ok(ApiResponse.success("알림을 읽음 처리했습니다.", response));
    }
}
