package faithcoderlab.tablebookingservice.domain.store.controller;

import faithcoderlab.tablebookingservice.domain.store.dto.StoreRecommendationDto;
import faithcoderlab.tablebookingservice.domain.store.service.StoreRecommendationService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 매장 추천 컨트롤러 클래스
 * 인기 매장 추천 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/stores/recommendations")
@RequiredArgsConstructor
public class StoreRecommendationController {

    private final StoreRecommendationService storeRecommendationService;

    /**
     * 인기 매장 추천 API
     * 예약 수, 평점, 리뷰 수 등을 기준으로 인기 매장 추천
     *
     * @param sortBy 정렬 기준 (rating, reservation, review)
     * @param limit  결과 개수 제한
     * @return 추천 매장 목록 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreRecommendationDto.Response>>> getRecommendedStores(
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        StoreRecommendationDto.RecommendationCriteria criteria = StoreRecommendationDto.RecommendationCriteria.builder()
                .sortBy(sortBy)
                .limit(limit).build();

        List<StoreRecommendationDto.Response> recommendations =
                storeRecommendationService.getRecommendedStores(criteria);

        return ResponseEntity.ok(ApiResponse.success("인기 매장 목록을 성공적으로 조회했습니다.", recommendations));
    }
}
