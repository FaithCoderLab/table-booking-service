package faithcoderlab.tablebookingservice.domain.partner.service;

import faithcoderlab.tablebookingservice.domain.UserRole;
import faithcoderlab.tablebookingservice.domain.partner.dto.PartnerDto;
import faithcoderlab.tablebookingservice.domain.partner.entity.Partner;
import faithcoderlab.tablebookingservice.domain.partner.repository.PartnerRepository;
import faithcoderlab.tablebookingservice.domain.user.repository.UserRepository;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 파트너(점장) 서비스 클래스
 * 파트너 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 파트너 회원가입 처리 메서드
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과 정보
     */
    @Transactional
    public PartnerDto.SignUpResponse signUp(PartnerDto.SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) || partnerRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByPhone(request.getPhone()) || partnerRepository.existsByPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        if (partnerRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            throw new CustomException(ErrorCode.BUSINESS_NUMBER_ALREADY_EXISTS);
        }

        Partner partner = Partner.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .businessNumber(request.getBusinessNumber())
                .businessName(request.getBusinessName())
                .address(request.getAddress())
                .role(UserRole.ROLE_PARTNER)
                .active(true)
                .build();

        Partner savedPartner = partnerRepository.save(partner);

        return PartnerDto.SignUpResponse.builder()
                .partnerId(savedPartner.getId())
                .email(savedPartner.getEmail())
                .name(savedPartner.getName())
                .phone(savedPartner.getPhone())
                .businessNumber(savedPartner.getBusinessNumber())
                .businessName(savedPartner.getBusinessName())
                .address(savedPartner.getAddress())
                .role(savedPartner.getRole()).build();
    }

    /**
     * 파트너 정보 조회 메서드
     *
     * @param partnerId 파트너 ID
     * @return 파트너 정보 응답 객체
     */
    @Transactional(readOnly = true)
    public PartnerDto.PartnerInfoResponse getPartnerInfo(Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new CustomException((ErrorCode.USER_NOT_FOUND)));

        return convertToPartnerInfoResponse(partner);
    }

    /**
     * 파트너 정보 수정 메서드
     *
     * @param partnerId 파트너 ID
     * @param request 수정 요청 정보
     * @return 수정된 파트너 정보 응답 객체
     */
    @Transactional
    public PartnerDto.UpdateResponse updatePartnerInfo(Long partnerId, PartnerDto.UpdateRequest request) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new CustomException((ErrorCode.USER_NOT_FOUND)));

        if (!partner.getPhone().equals(request.getPhone()) &&
                (partnerRepository.existsByPhone(request.getPhone()) || userRepository.existsByPhone(request.getPhone()))) {
            throw new CustomException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        partner.setName(request.getName());
        partner.setPhone(request.getPhone());
        partner.setBusinessName(request.getBusinessName());
        partner.setAddress(request.getAddress());

        Partner updatedPartner = partnerRepository.save(partner);

        return PartnerDto.UpdateResponse.builder()
                .partnerId(updatedPartner.getId())
                .email(updatedPartner.getEmail())
                .name(updatedPartner.getName())
                .phone(updatedPartner.getPhone())
                .businessNumber(updatedPartner.getBusinessNumber())
                .businessName(updatedPartner.getBusinessName())
                .address(updatedPartner.getAddress())
                .role(updatedPartner.getRole())
                .updatedAt(updatedPartner.getUpdatedAt())
                .build();
    }

    /**
     * Partner 엔티티를 PartnerInfoResponse DTO로 변환
     *
     * @param partner Partner 엔티티
     * @return PartnerInfoResponse DTO
     */
    private PartnerDto.PartnerInfoResponse convertToPartnerInfoResponse(Partner partner) {
        return PartnerDto.PartnerInfoResponse.builder()
                .partnerId(partner.getId())
                .email(partner.getEmail())
                .name(partner.getName())
                .phone(partner.getPhone())
                .businessNumber(partner.getBusinessNumber())
                .businessName(partner.getBusinessName())
                .address(partner.getAddress())
                .role(partner.getRole())
                .createdAt(partner.getCreatedAt())
                .updatedAt(partner.getUpdatedAt())
                .build();
    }
}
