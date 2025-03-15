package faithcoderlab.tablebookingservice.domain.auth.service;

import faithcoderlab.tablebookingservice.domain.auth.dto.AuthDto;
import faithcoderlab.tablebookingservice.domain.partner.entity.Partner;
import faithcoderlab.tablebookingservice.domain.partner.repository.PartnerRepository;
import faithcoderlab.tablebookingservice.domain.user.entity.User;
import faithcoderlab.tablebookingservice.domain.user.repository.UserRepository;
import faithcoderlab.tablebookingservice.global.config.JwtUtil;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스 클래스
 * 로그인 및 토큰 발급 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 로그인 처리 메서드
     * 사용자와 파트너 테이블 모두에서 이메일 검색 후 로그인 처리
     *
     * @param request 로그인 요청 정보
     * @return 로그인 결과 정보 (JWT 토큰 포함)
     */
    @Transactional(readOnly = true)
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user != null) {
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            return AuthDto.LoginResponse.builder()
                    .token(token)
                    .role(user.getRole().name())
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .build();
        }

        Partner partner = partnerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), partner.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.generateToken(partner.getEmail(), partner.getRole().name());

        return AuthDto.LoginResponse.builder()
                .token(token)
                .role(partner.getRole().name())
                .id(partner.getId())
                .email(partner.getEmail())
                .name(partner.getName())
                .build();
    }
}
