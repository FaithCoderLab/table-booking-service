package faithcoderlab.tablebookingservice.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StoreRecommendationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long storeId;
        private String name;
        private String address;
        private String description;
        private String phoneNumber;
        private String businessHours;
        private Double averageRating;
        private Long reviewCount;
        private Long reservationCount;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationCriteria {
        private String sortBy;
        private Integer limit;
    }
}
