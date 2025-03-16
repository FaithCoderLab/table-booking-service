package faithcoderlab.tablebookingservice.domain.reservation.controller;

import faithcoderlab.tablebookingservice.domain.reservation.dto.ArrivalDto;
import faithcoderlab.tablebookingservice.domain.reservation.service.ArrivalService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 도착 확인 컨트롤러 클래스
 * 키오스크를 통한 도착 확인 API 처리
 */
@RestController
@RequestMapping("/api/kiosk")
@RequiredArgsConstructor
public class ArrivalController {

    private final ArrivalService arrivalService;

    /**
     * 도착 확인 API
     * 키오스크에서 사용자가 도착 확인 시 호출되는 API
     *
     * @param request 도착 확인 요청 정보
     * @return 도착 확인 결과 응답
     */
    @PostMapping("/arrival")
    public ResponseEntity<ApiResponse<ArrivalDto.ArrivalResponse>> confirmArrival(
            @Valid @RequestBody ArrivalDto.ArrivalRequest request
    ) {
        ArrivalDto.ArrivalResponse response = arrivalService.confirmArrival(request);
        return ResponseEntity.ok(ApiResponse.success("도착 확인이 성공적으로 처리되었습니다.", response));
    }
}
