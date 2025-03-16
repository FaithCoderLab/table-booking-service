package faithcoderlab.tablebookingservice.domain.notification.repository;

import faithcoderlab.tablebookingservice.domain.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 알림 레포지토리 인터페이스
 * 알림 데이터 접근 인터페이스
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 사용자 ID로 알림 목록 조회(최신순)
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 알림 목록
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 사용자의 읽지 않은 알림 수 카운트
     *
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 수
     */
    long countByUserIdAndReadFalse(Long userId);

    /**
     * 사용자 ID와 알림 타입으로 알림 목록 조회
     *
     * @param userId 사용자 ID
     * @param type   알림 타입
     * @return 알림 목록
     */
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);

    /**
     * 사용자 ID와 참조 ID로 알림 조회
     *
     * @param userId 사용자 ID
     * @param referenceId 참조 ID
     * @param type 알림 타입
     * @return 알림 목록
     */
    List<Notification> findByUserIdAndReferenceIdAndType(Long userId, Long referenceId, String type);
}
