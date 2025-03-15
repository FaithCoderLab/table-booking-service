package faithcoderlab.tablebookingservice.domain.user.service;

import faithcoderlab.tablebookingservice.domain.user.dto.UserDto;
import faithcoderlab.tablebookingservice.domain.user.entity.User;
import faithcoderlab.tablebookingservice.domain.UserRole;
import faithcoderlab.tablebookingservice.domain.user.repository.UserRepository;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 일반 사용자 서비스 클래스
 * 사용자 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 일반 사용자 회원가입 처리 메서드
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과 정보
     */
    @Transactional
    public UserDto.SignUpResponse signUp(UserDto.SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(UserRole.ROLE_USER)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return UserDto.SignUpResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole())
                .build();
    }

    /**
     * 사용자 정보 조회 메서드
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 응답 객체
     */
    @Transactional(readOnly = true)
    public UserDto.UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return convertToUserInfoResponse(user);
    }

    /**
     * 사용자 정보 수정 메서드
     *
     * @param userId 사용자 ID
     * @param request 수정 요청 정보
     * @return 수정된 사용자 정보 응답 객체
     */
    @Transactional
    public UserDto.UpdateResponse updateUserInfo(Long userId, UserDto.UpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.getPhone().equals(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        user.setName(request.getName());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);

        return UserDto.UpdateResponse.builder()
                .userId(updatedUser.getId())
                .email(updatedUser.getEmail())
                .name(updatedUser.getName())
                .phone(updatedUser.getPhone())
                .role(updatedUser.getRole())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
    }

    /**
     * User 엔티티를 UserInfoRespones DTO로 변환
     *
     * @param user User 엔티티
     * @return UserInfoResponse DTO
     */
    private UserDto.UserInfoResponse convertToUserInfoResponse(User user) {
        return UserDto.UserInfoResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
