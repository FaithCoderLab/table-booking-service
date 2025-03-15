package faithcoderlab.tablebookingservice.domain.user.controller;

import faithcoderlab.tablebookingservice.domain.user.dto.UserDto;
import faithcoderlab.tablebookingservice.domain.user.service.UserService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import faithcoderlab.tablebookingservice.global.security.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 일반 사용자 컨트롤러 클래스
 * 사용자 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationUtil authenticationUtil;

    /**
     * 일반 사용자 회원가입 API
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과 응답
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDto.SignUpResponse>> signUp(
            @Valid @RequestBody UserDto.SignUpRequest request
    ) {
        UserDto.SignUpResponse response = userService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 성공적으로 완료되었습니다.", response));
    }

    /**
     * 사용자 정보 조회 API
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 응답
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto.UserInfoResponse>> getUserInfo(
            @PathVariable Long userId
    ) {
        UserDto.UserInfoResponse userInfo = userService.getUserInfo(userId);

        authenticationUtil.validateOwnership(userId, userInfo.getEmail());

        return ResponseEntity.ok(ApiResponse.success("사용자 정보를 성공적으로 조회했습니다.", userInfo));
    }

    /**
     * 사용자 정보 수정 API
     *
     * @param userId 사용자 ID
     * @param request 수정 요청 정보
     * @return 수정된 사용자 정보 응답
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto.UpdateResponse>> updateUserInfo(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto.UpdateRequest request
    ) {
        UserDto.UserInfoResponse userInfo = userService.getUserInfo(userId);
        authenticationUtil.validateOwnership(userId, userInfo.getEmail());

        UserDto.UpdateResponse response = userService.updateUserInfo(userId, request);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보가 성공적으로 수정되었습니다.", response));
    }
}
