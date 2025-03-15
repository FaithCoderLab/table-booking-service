package faithcoderlab.tablebookingservice.domain.partner.dto;

import faithcoderlab.tablebookingservice.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 파트너(점장) DTO 클래스
 * 파트너 회원가입 요청 정보를 담는 DTO
 */
public class PartnerDto {

    /**
     * 파트너 회원가입 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpRequest {

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String password;

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        private String name;

        @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10-11자리의 숫자로만 이루어져야 합니다.")
        private String phone;

        @NotBlank(message = "사업자등록번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^\\d{10}$", message = "사업자등록번호는 10자리의 숫자로만 이루어져야 합니다.")
        private String businessNumber;

        private String businessName;
        private String address;
    }

    /**
     * 파트너 회원가입 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpResponse {
        private Long partnerId;
        private String email;
        private String name;
        private String phone;
        private String businessNumber;
        private String businessName;
        private String address;
        private UserRole role;
    }

    /**
     * 파트너 정보 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartnerInfoResponse {
        private Long partnerId;
        private String email;
        private String name;
        private String phone;
        private String businessNumber;
        private String businessName;
        private String address;
        private UserRole role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * 파트너 정보 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        private String name;

        @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10-11자리의 숫자로만 이루어져야 합니다.")
        private String phone;

        private String businessName;
        private String address;
    }

    /**
     * 파트너 정보 수정 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateResponse {
        private Long partnerId;
        private String email;
        private String name;
        private String phone;
        private String businessNumber;
        private String businessName;
        private String address;
        private UserRole role;
        private LocalDateTime updatedAt;
    }
}
