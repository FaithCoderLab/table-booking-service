package faithcoderlab.tablebookingservice.domain.reservation.controller;

import faithcoderlab.tablebookingservice.domain.reservation.dto.ReservationDto;
import faithcoderlab.tablebookingservice.domain.reservation.service.ReservationService;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import faithcoderlab.tablebookingservice.global.security.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 예약 관련 컨트롤러 클래스
 * 예약 관련 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final AuthenticationUtil authenticationUtil;

    /**
     * 예약 가능 시간 조회 API
     * 매장별, 날짜별 예약 가능한 시간 목록을 조회
     *
     * @param request 예약 가능 시간 조회 요청 정보
     * @return 예약 가능 시간 목록 응답
     */
    @PostMapping("/available-times")
    public ResponseEntity<ApiResponse<ReservationDto.AvailableTimesResponse>> getAvailableTimes(
            @Valid @RequestBody ReservationDto.AvailableTimesRequest request
    ) {
        ReservationDto.AvailableTimesResponse response = reservationService.getAvailableTimes(request);

        return ResponseEntity.ok(ApiResponse.success("예약 가능 시간 목록을 성공적으로 조회했습니다.", response));
    }

    /**
     * 예약 생성 API
     * 사용자가 매장에 예약 요청
     *
     * @param request 예약 생성 요청 정보
     * @return 생성된 예약 정보 응답
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationDto.CreateResponse>> createReservation(
            @Valid @RequestBody ReservationDto.CreateRequest request
    ) {
        String userEmail = authenticationUtil.getCurrentUserEmail();

        Long userId = authenticationUtil.getCurrentUserId();

        ReservationDto.CreateResponse response = reservationService.createReservation(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("예약이 성공적으로 생성되었습니다.", response));

    }
}
