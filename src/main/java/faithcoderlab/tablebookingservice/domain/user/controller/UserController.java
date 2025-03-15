package faithcoderlab.tablebookingservice.domain.user.controller;

import faithcoderlab.tablebookingservice.domain.user.dto.UserDto;
import faithcoderlab.tablebookingservice.domain.user.service.UserService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 일반 사용자 컨트롤러 클래스
 * 사용자 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
}
