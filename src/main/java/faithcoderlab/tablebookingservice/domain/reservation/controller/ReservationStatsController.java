package faithcoderlab.tablebookingservice.domain.reservation.controller;

import faithcoderlab.tablebookingservice.domain.reservation.dto.ReservationStatsDto;
import faithcoderlab.tablebookingservice.domain.reservation.service.ReservationStatsService;
import faithcoderlab.tablebookingservice.domain.store.entity.Store;
import faithcoderlab.tablebookingservice.domain.store.repository.StoreRepository;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import faithcoderlab.tablebookingservice.global.security.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 예약 통계 컨트롤러 클래스
 * 매장별 예약 통계 API 엔드포인트 처리
 */
@RestController
@RequestMapping("/api/stats/reservations")
@RequiredArgsConstructor
public class ReservationStatsController {

    private final ReservationStatsService reservationStatsService;
    private final StoreRepository storeRepository;
    private final AuthenticationUtil authenticationUtil;

    /**
     * 기간별 예약 통계 API
     * 특정 매장의 날짜 범위 내 예약 통계 정보 제공
     *
     * @param storeId   매장 ID
     * @param partnerId 파트너 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 기간별 예약 통계 응답
     */
    @GetMapping("/period/stores/{storeId}/partners/{partnerId}")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<ReservationStatsDto.PeriodStatsResponse>> getPeriodStats(
            @PathVariable Long storeId,
            @PathVariable Long partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateStoreOwnership(storeId, partnerId);

        ReservationStatsDto.PeriodStatsResponse response =
                reservationStatsService.getPeriodStats(storeId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("기간별 예약 통계를 성공적으로 조회했습니다.", response));
    }

    /**
     * 시간대별 예약 통계 API
     * 특정 매장의 날짜 범위 내 시간대별 예약 통계 정보 제공
     *
     * @param storeId   매장 ID
     * @param partnerId 파트너 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 시간대별 예약 통계 응답
     */
    @GetMapping("/timeslot/stores/{storeId}/partners/{partnerId}")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<ReservationStatsDto.TimeSlotStatsResponse>> getTimeSlotStats(
            @PathVariable Long storeId,
            @PathVariable Long partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateStoreOwnership(storeId, partnerId);

        ReservationStatsDto.TimeSlotStatsResponse response =
                reservationStatsService.getTimeSlotStats(storeId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("시간대별 예약 통계를 성공적으로 조회했습니다.", response));
    }

    /**
     * 상태별 예약 통계 API
     * 특정 매장의 날짜 범위 내 예약 상태별 통계 정보 제공
     *
     * @param storeId   매장 ID
     * @param partnerId 파트너 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 상태별 예약 통계 응답
     */
    @GetMapping("/status/stores/{storeId}/partners/{partnerId}")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<ReservationStatsDto.StatusStatsResponse>> getStatusStats(
            @PathVariable Long storeId,
            @PathVariable Long partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateStoreOwnership(storeId, partnerId);

        ReservationStatsDto.StatusStatsResponse response =
                reservationStatsService.getStatusStats(storeId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("상태별 예약 통계를 성공적으로 조회했습니다.", response));
    }

    /**
     * 매장 소유권 검증 메서드
     * 현재 로그인한 파트너가 해장 매당의 소유자인지 확인
     *
     * @param storeId   매장 ID
     * @param partnerId 파트너 ID
     * @throws CustomException 권한이 없을 경우 발생하는 예외
     */
    private void validateStoreOwnership(Long storeId, Long partnerId) {
        authenticationUtil.validatePartnerOwnership(partnerId);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getPartner().getId().equals(partnerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "해당 매장에 대한 접근 권한이 없습니다.");
        }
    }
}
