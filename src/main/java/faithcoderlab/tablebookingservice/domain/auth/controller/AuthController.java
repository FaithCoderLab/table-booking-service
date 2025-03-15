package faithcoderlab.tablebookingservice.domain.auth.controller;

import faithcoderlab.tablebookingservice.domain.auth.dto.AuthDto;
import faithcoderlab.tablebookingservice.domain.auth.service.AuthService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 컨트롤러 클래스
 * 로그인 및 인증 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API
     * 이메일과 비밀번호를 검증하고 JWT 토큰 발급
     *
     * @param request 로그인 요청 정보
     * @return 로그인 결과 응답 (JWT 토큰 포함)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공했습니다.", response));
    }
}
