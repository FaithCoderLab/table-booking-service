package faithcoderlab.tablebookingservice.domain.partner.controller;

import faithcoderlab.tablebookingservice.domain.partner.dto.PartnerDto;
import faithcoderlab.tablebookingservice.domain.partner.service.PartnerService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import faithcoderlab.tablebookingservice.global.security.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 파트너(점장) 컨트롤러 클래스
 * 파트너 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;
    private final AuthenticationUtil authenticationUtil;

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

    /**
     * 파트너 정보 조회 API
     *
     * @param partnerId 파트너 ID
     * @return 파트너 정보 응답
     */
    @GetMapping("/{partnerId}")
    public ResponseEntity<ApiResponse<PartnerDto.PartnerInfoResponse>> getPartnerInfo(
            @PathVariable Long partnerId
    ) {
        PartnerDto.PartnerInfoResponse partnerInfo = partnerService.getPartnerInfo(partnerId);

        authenticationUtil.validateOwnership(partnerId, partnerInfo.getEmail());

        return ResponseEntity.ok(ApiResponse.success("파트너 정보를 성공적으로 조회했습니다.", partnerInfo));
    }

    /**
     * 파트너 정보 수정 API
     *
     * @param partnerId 파트너 ID
     * @param request   수정 요청 정보
     * @return 수정된 파트너 정보 응답
     */
    @PutMapping("/{partnerId}")
    public ResponseEntity<ApiResponse<PartnerDto.UpdateResponse>> updatePartnerInfo(
            @PathVariable Long partnerId,
            @Valid @RequestBody PartnerDto.UpdateRequest request
    ) {
        PartnerDto.PartnerInfoResponse partnerInfo = partnerService.getPartnerInfo(partnerId);
        authenticationUtil.validateOwnership(partnerId, partnerInfo.getEmail());

        PartnerDto.UpdateResponse response = partnerService.updatePartnerInfo(partnerId, request);
        return ResponseEntity.ok(ApiResponse.success("파트너 정보가 성공적으로 수정되었습니다.", response));
    }
}
