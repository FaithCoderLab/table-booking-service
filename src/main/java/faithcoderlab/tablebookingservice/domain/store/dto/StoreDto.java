package faithcoderlab.tablebookingservice.domain.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 매장 DTO 클래스
 * 매장 관련 데이터 전송 객체
 */
public class StoreDto {

    /**
     * 매장 등록 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "매장 이름은 필수 입력 항목입니다.")
        private String name;

        @NotBlank(message = "매장 주소는 필수 입력 항목입니다.")
        private String address;

        private String description;
        private String phoneNumber;
        private String businessHours;
    }

    /**
     * 매장 등록 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResponse {
        private Long storeId;
        private String name;
        private String address;
        private String description;
        private String phoneNumber;
        private String businessHours;
        private Long partnerId;
        private LocalDateTime createdAt;
    }

    /**
     * 매장 조회 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreInfoResponse {
        private Long storeId;
        private String name;
        private String address;
        private String description;
        private String phoneNumber;
        private String businessHours;
        private Long partnerId;
        private String partnerName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * 매장 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "매장 이름은 필수 입력 항목입니다.")
        private String name;

        @NotBlank(message = "매장 주소는 필수 입력 항목입니다.")
        private String address;

        private String description;
        private String phoneNumber;
        private String businessHours;
    }

    /**
     * 매장 수정 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateResponse {
        private Long storeId;
        private String name;
        private String address;
        private String description;
        private String phoneNumber;
        private String businessHours;
        private Long partnerId;
        private LocalDateTime updatedAt;
    }
}
