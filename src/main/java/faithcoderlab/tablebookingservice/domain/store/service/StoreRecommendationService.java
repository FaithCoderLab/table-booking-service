package faithcoderlab.tablebookingservice.domain.store.service;

import faithcoderlab.tablebookingservice.domain.reservation.repository.ReservationRepository;
import faithcoderlab.tablebookingservice.domain.review.repository.ReviewRepository;
import faithcoderlab.tablebookingservice.domain.store.dto.StoreRecommendationDto;
import faithcoderlab.tablebookingservice.domain.store.entity.Store;
import faithcoderlab.tablebookingservice.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreRecommendationService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 인기 매장 추천 메서드
     * 예약 수, 평점, 리뷰 수 등을 기준으로 인기 매장 추천
     *
     * @param criteria 추천 기준 정보
     * @return 추천 매장 목록
     */
    @Transactional(readOnly = true)
    public List<StoreRecommendationDto.Response> getRecommendedStores(StoreRecommendationDto.RecommendationCriteria criteria) {
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy().toLowerCase() : "rating";
        int limit = criteria.getLimit() != null ? criteria.getLimit() : 10;

        List<Store> stores = storeRepository.findAllByActiveOrderByNameAsc(true);

        List<StoreRecommendationDto.Response> recommendationResponses = new java.util.ArrayList<>(stores.stream()
                .map(store -> {
                    Long storeId = store.getId();
                    Double averageRating = reviewRepository.getAverageRatingByStoreId(storeId);
                    Long reviewCount = reviewRepository.countByStoreIdAndActive(storeId, true);
                    Long reservationCount = reservationRepository.countByStoreId(storeId);

                    if (averageRating == null) {
                        averageRating = 0.0;
                    }

                    return StoreRecommendationDto.Response.builder()
                            .storeId(storeId)
                            .name(store.getName())
                            .address(store.getAddress())
                            .description(store.getDescription())
                            .phoneNumber(store.getPhoneNumber())
                            .businessHours(store.getBusinessHours())
                            .averageRating(averageRating)
                            .reviewCount(reviewCount)
                            .reservationCount(reservationCount)
                            .latitude(store.getLatitude())
                            .longitude(store.getLongitude())
                            .build();
                })
                .toList());

        switch (sortBy) {
            case "reservation":
                recommendationResponses.sort((a, b) -> b.getReservationCount().compareTo(a.getReservationCount()));
                break;
            case "review":
                recommendationResponses.sort((a, b) -> b.getReviewCount().compareTo(a.getReviewCount()));
                break;
            case "rating":
            default:
                recommendationResponses.sort((a, b) -> b.getAverageRating().compareTo(a.getAverageRating()));
                break;
        }

        return recommendationResponses.stream()
                .limit(limit)
                .collect(Collectors.toList());

    }
}
