package faithcoderlab.tablebookingservice.global.security;

import faithcoderlab.tablebookingservice.domain.partner.entity.Partner;
import faithcoderlab.tablebookingservice.domain.partner.repository.PartnerRepository;
import faithcoderlab.tablebookingservice.domain.user.repository.UserRepository;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 인증 유틸리티 클래스
 * 현재 로그인한 사용자의 정보를 확인하거나 권한을 검증하는 메서드 제공
 */
@Component
public class AuthenticationUtil {

    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;

    public AuthenticationUtil(PartnerRepository partnerRepository, UserRepository userRepository) {
        this.partnerRepository = partnerRepository;
        this.userRepository = userRepository;
    }

    /**
     * 현재 인증된 사용자의 이메일 반환
     *
     * @return 인증된 사용자 이메일
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return authentication.getName();
    }

    /**
     * 현재 인증된 사용자가 해당 ID의 소유자인지 확인
     * 자신의 정보만 수정할 수 있도록 권한 체크
     *
     * @param id    확인할 ID
     * @param email 비교할 이메일
     */
    public void validateOwnership(Long id, String email) {
        String currentUserEmail = getCurrentUserEmail();
        if (!currentUserEmail.equals(email)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 현재 인증된 사용자가 해당 파트너인지 확인
     *
     * @param partnerId 파트너 ID
     */
    public void validatePartnerOwnership(Long partnerId) {
        String currentUserEmail = getCurrentUserEmail();

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!currentUserEmail.equals(partner.getEmail())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    public Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();

        boolean isPartner = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PARTNER"));

        if (isPartner) {
            return partnerRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                    .getId();
        } else {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                    .getId();
        }
    }
}
