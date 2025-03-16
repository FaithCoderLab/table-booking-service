package faithcoderlab.tablebookingservice.domain.review.controller;

import faithcoderlab.tablebookingservice.domain.review.dto.ReviewDto;
import faithcoderlab.tablebookingservice.domain.review.service.ReviewService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import faithcoderlab.tablebookingservice.global.security.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * 리뷰 컨트롤러 클래스
 * 리뷰 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthenticationUtil authenticationUtil;

    /**
     * 리뷰 생성 API
     * 예약 이용 후 리뷰 작성
     *
     * @param request 리뷰 생성 요청 정보
     * @return 생성된 리뷰 정보 응답
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto.Response>> createReview(
            @Valid @RequestBody ReviewDto.CreateRequest request
    ) {
        Long userId = authenticationUtil.getCurrentUserId();
        ReviewDto.Response response = reviewService.createReview(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰가 성공적으로 작성되었습니다.", response));
    }

    /**
     * 매장별 리뷰 목록 조회 API
     *
     * @param storeId 매장 ID
     * @return 리뷰 목록 응답
     */
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<ApiResponse<List<ReviewDto.Response>>> getReviewsByStoreId(
            @PathVariable("storeId") Long storeId
    ) {
        List<ReviewDto.Response> reviews = reviewService.getReviewsByStoreId(storeId);

        return ResponseEntity.ok(ApiResponse.success("리뷰 목록을 성공적으로 조회했습니다.", reviews));
    }

    /**
     * 사용자 작성 리뷰 목록 조회 API
     *
     * @return 사용자가 작성한 리뷰 목록 응답
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<ReviewDto.Response>>> getUserReviews() {
        Long userId = authenticationUtil.getCurrentUserId();
        List<ReviewDto.Response> reviews = reviewService.getReviewsByUserId(userId);

        return ResponseEntity.ok(ApiResponse.success("내가 작성한 리뷰 목록을 성공적으로 조회했습니다.", reviews));
    }

    /**
     * 리뷰 상세 조회 API
     *
     * @param reviewId 리뷰 ID
     * @return 리뷰 상세 정보 응답
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto.Response>> getReviewDetail(
            @PathVariable("reviewId") Long reviewId
    ) {
        ReviewDto.Response review = reviewService.getReviewDetail(reviewId);

        return ResponseEntity.ok(ApiResponse.success("리뷰 상세 정보를 성공적으로 조회했습니다.", review));
    }

    /**
     * 리뷰 수정 API
     * 리뷰 작성자만 수정 가능
     *
     * @param reviewId 리뷰 ID
     * @param request  리뷰 수정 요청 정보
     * @return 수정된 리뷰 정보 응답
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto.Response>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDto.UpdateRequest request
    ) {
        Long userId = authenticationUtil.getCurrentUserId();
        ReviewDto.Response response = reviewService.updateReview(userId, reviewId, request);

        return ResponseEntity.ok(ApiResponse.success("리뷰가 성공적으로 수정되었습니다.", response));
    }

    /**
     * 리뷰 삭제 API
     * 리뷰 작성자 또는 매장 관리자(파트너)만 삭제 가능
     *
     * @param reviewId 리뷰 ID
     * @return 삭제 결과 응답
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteReview(
            @PathVariable Long reviewId
    ) {
        Long userId = authenticationUtil.getCurrentUserId();

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();
        boolean isPartner = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PARTNER"));

        boolean result = reviewService.deleteReview(userId, reviewId, isPartner);

        return ResponseEntity.ok(ApiResponse.success("리뷰가 성공적으로 삭제되었습니다.", result));
    }

    /**
     * 매장 평균 평점 조회 API
     *
     * @param storeId 매장 ID
     * @return 평균 평점 응답
     */
    @GetMapping("/stores/{storeId}/rating")
    public ResponseEntity<ApiResponse<Double>> getStoreAverageRating(
            @PathVariable Long storeId
    ) {
        Double averageRating = reviewService.getAverageRatingByStoreId(storeId);

        if (averageRating == null) {
            averageRating = 0.0;
        }

        return ResponseEntity.ok(ApiResponse.success("매장 평균 평점을 성공적으로 조회했습니다.", averageRating));
    }

    /**
     * 매장 리뷰 수 조회 API
     *
     * @param storeId 매장 ID
     * @return 리뷰 수 응답
     */
    @GetMapping("/stores/{storeId}/count")
    public ResponseEntity<ApiResponse<Long>> getStoreReviewCount(
            @PathVariable Long storeId
    ) {
        long reviewCount = reviewService.getReviewCountByStoreId(storeId);

        return ResponseEntity.ok(ApiResponse.success("매장 리뷰 수를 성공적으로 조회했습니다.", reviewCount));
    }
}
