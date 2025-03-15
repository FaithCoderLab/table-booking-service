package faithcoderlab.tablebookingservice.domain.partner.controller;

import faithcoderlab.tablebookingservice.domain.partner.dto.PartnerDto;
import faithcoderlab.tablebookingservice.domain.partner.service.PartnerService;
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
 * 파트너(점장) 컨트롤러 클래스
 * 파트너 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    /**
     * 파트너 회원가입 API
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과 응답
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<PartnerDto.SignUpResponse>> signUp(
            @Valid @RequestBody PartnerDto.SignUpRequest request
    ) {
        PartnerDto.SignUpResponse response = partnerService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("파트너 회원가입이 성공적으로 완료되었습니다.", response));
    }
}
