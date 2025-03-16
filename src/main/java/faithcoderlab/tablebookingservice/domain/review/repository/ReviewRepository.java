package faithcoderlab.tablebookingservice.domain.review.repository;

import faithcoderlab.tablebookingservice.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 레포지토리 인터페이스
 * 리뷰 데이터 접근 인터페이스
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 매장 ID별 리뷰 목록 조회
     *
     * @param storeId 매장 ID
     * @return 리뷰 목록
     */
    List<Review> findByStoreIdAndActiveOrderByCreatedAtDesc(Long storeId, boolean active);

    /**
     * 사용자 ID별 리뷰 목록 조회
     *
     * @param userId 사용자 ID
     * @return 리뷰 목록
     */
    List<Review> findByUserIdAndActiveOrderByCreatedAtDesc(Long userId, boolean active);

    /**
     * 예약 ID로 리뷰 조회
     *
     * @param reservationId 예약 ID
     * @return 리뷰 Optional 객체
     */
    Optional<Review> findByReservationId(Long reservationId);

    /**
     * 매장 ID별 평균 평점 조회
     *
     * @param storeId 매장 ID
     * @return 평균 평점
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.store.id = :storeId AND r.active = true")
    Double getAverageRatingByStoreId(@Param("storeId") Long storeId);

    /**
     * 매장 ID별 리뷰 수 조회
     *
     * @param storeId 매장 ID
     * @return 리뷰 수
     */
    long countByStoreIdAndActive(Long storeId, boolean active);
}
