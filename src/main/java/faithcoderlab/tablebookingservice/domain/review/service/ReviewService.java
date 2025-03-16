package faithcoderlab.tablebookingservice.domain.review.service;

import faithcoderlab.tablebookingservice.domain.reservation.entity.Reservation;
import faithcoderlab.tablebookingservice.domain.reservation.entity.ReservationStatus;
import faithcoderlab.tablebookingservice.domain.reservation.repository.ReservationRepository;
import faithcoderlab.tablebookingservice.domain.review.dto.ReviewDto;
import faithcoderlab.tablebookingservice.domain.review.entity.Review;
import faithcoderlab.tablebookingservice.domain.review.repository.ReviewRepository;
import faithcoderlab.tablebookingservice.domain.store.entity.Store;
import faithcoderlab.tablebookingservice.domain.user.entity.User;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 서비스 클래스
 * 리뷰 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 리뷰 생성 메서드
     * 예약 완료 상태인 경우에만 리뷰 작성 가능
     *
     * @param userId  사용자 ID
     * @param request 리뷰 생성 요청 정보
     * @return 생성된 리뷰 정보
     */
    @Transactional
    public ReviewDto.Response createReview(Long userId, ReviewDto.CreateRequest request) {
        Reservation reservation = reservationRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "완료된 예약에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "본인의 예약에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        if (reviewRepository.findByReservationId(reservation.getId()).isPresent()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "이미 리뷰가 작성된 예약입니다.");
        }

        User user = reservation.getUser();
        Store store = reservation.getStore();

        Review review = Review.builder()
                .user(user)
                .store(store)
                .reservation(reservation)
                .rating(request.getRating())
                .content(request.getContent())
                .active(true)
                .build();

        Review savedReview = reviewRepository.save(review);

        return convertToResponseDto(savedReview);
    }

    /**
     * 매장별 리뷰 목록 조회 메서드
     *
     * @param storeId 매장 ID
     * @return 리뷰 목록
     */
    @Transactional(readOnly = true)
    public List<ReviewDto.Response> getReviewsByStoreId(Long storeId) {
        List<Review> reviews = reviewRepository.findByStoreIdAndActiveOrderByCreatedAtDesc(storeId, true);

        return reviews.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자별 리뷰 목록 조회 메서드
     *
     * @param userId 사용자 ID
     * @return 리뷰 목록
     */
    @Transactional(readOnly = true)
    public List<ReviewDto.Response> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserIdAndActiveOrderByCreatedAtDesc(userId, true);

        return reviews.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 상세 조회 메서드
     *
     * @param reviewId 리뷰 ID
     * @return 리뷰 상세 정보
     */
    @Transactional(readOnly = true)
    public ReviewDto.Response getReviewDetail(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.isActive()) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }

        return convertToResponseDto(review);
    }

    /**
     * 리뷰 수정 메서드
     * 리뷰 작성자만 수정 가능
     *
     * @param reviewId 리뷰 ID
     * @param userId   사용자 ID
     * @param request  리뷰 수정 요청 정보
     * @return 수정된 리뷰 정보
     */
    @Transactional
    public ReviewDto.Response updateReview(Long userId, Long reviewId, ReviewDto.UpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.isActive()) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_REVIEW_OWNER);
        }

        review.setRating(request.getRating());
        review.setContent(request.getContent());

        Review updatedReview = reviewRepository.save(review);

        return convertToResponseDto(updatedReview);
    }

    /**
     * 리뷰 삭제 메서드
     * 리뷰 작성자 또는 매장 관리자(파트너)만 삭제 가능
     *
     * @param reviewId  리뷰 ID
     * @param userId    사용자 ID
     * @param isPartner 파트너 여부
     * @return 성공 여부
     */
    @Transactional
    public boolean deleteReview(Long reviewId, Long userId, boolean isPartner) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.isActive()) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }

        boolean isReviewOwner = review.getUser().getId().equals(userId);
        boolean isStorePartner = isPartner && review.getStore().getPartner().getId().equals(userId);

        if (!isReviewOwner && !isStorePartner) {
            throw new CustomException(ErrorCode.INVALID_REVIEW_PERMISSION);
        }

        review.setActive(false);
        reviewRepository.save(review);

        return true;
    }

    /**
     * Review 엔티티를 Response DTO로 변환
     *
     * @param review Review 엔티티
     * @return Response DTO
     */
    private ReviewDto.Response convertToResponseDto(Review review) {
        return ReviewDto.Response.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .storeId(review.getStore().getId())
                .storeName(review.getStore().getName())
                .reservationId(review.getReservation().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    /**
     * 매장 평균 평점 조회 메서드
     *
     * @param storeId 매장 ID
     * @return 평균 평점
     */
    @Transactional(readOnly = true)
    public Double getAverageRatingByStoreId(Long storeId) {
        return reviewRepository.getAverageRatingByStoreId(storeId);
    }

    /**
     * 매장 리뷰 수 조회 메서드
     *
     * @param storeId 매장 ID
     * @return 리뷰 수
     */
    @Transactional(readOnly = true)
    public long getReviewCountByStoreId(Long storeId) {
        return reviewRepository.countByStoreIdAndActive(storeId, true);
    }


}
