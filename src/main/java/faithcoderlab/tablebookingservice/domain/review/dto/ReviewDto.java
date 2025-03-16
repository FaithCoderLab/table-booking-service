package faithcoderlab.tablebookingservice.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 DTO 클래스
 * 리뷰 관련 데이터 전송 객체
 */
public class ReviewDto {

    /**
     * 리뷰 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "예약 ID는 필수 입력 항목입니다.")
        private Long reservationId;

        @NotNull(message = "평점은 필수 입력 항목입니다.")
        @Min(value = 1, message = "평점은 최소 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 최대 5점까지 가능합니다.")
        private Integer rating;

        @NotBlank(message = "리뷰 내용은 필수 입력 항목입니다.")
        private String content;
    }

    /**
     * 리뷰 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long reviewId;
        private Long userId;
        private String userName;
        private Long storeId;
        private String storeName;
        private Long reservationId;
        private Integer rating;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * 리뷰 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotNull(message = "평점은 필수 입력 항목입니다.")
        @Min(value = 1, message = "평점은 최소 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 최대 5점까지 가능합니다.")
        private Integer rating;

        @NotBlank(message = "리뷰 내용은 필수 입력 항목입니다.")
        private String content;
    }
}
