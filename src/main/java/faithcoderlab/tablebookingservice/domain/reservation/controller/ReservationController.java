package faithcoderlab.tablebookingservice.domain.reservation.controller;

import faithcoderlab.tablebookingservice.domain.reservation.dto.ReservationDto;
import faithcoderlab.tablebookingservice.domain.reservation.service.ReservationService;
import faithcoderlab.tablebookingservice.domain.store.entity.Store;
import faithcoderlab.tablebookingservice.domain.store.repository.StoreRepository;
import faithcoderlab.tablebookingservice.global.common.ApiResponse;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import faithcoderlab.tablebookingservice.global.security.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

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
    private final StoreRepository storeRepository;

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

    /**
     * 사용자별 예약 목록 조회 API
     * 로그인한 사용자의 예약 목록을 조회
     *
     * @return 사용자의 예약 목록 응답
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<ReservationDto.ReservationInfoResponse>>> getUserReservations(
            @RequestParam(required = false) List<String> status
    ) {
        Long userId = authenticationUtil.getCurrentUserId();
        List<ReservationDto.ReservationInfoResponse> reservations = reservationService.getUserReservations(userId, status);

        return ResponseEntity.ok(ApiResponse.success("예약 목록을 성공적으로 조회했습니다.", reservations));
    }

    /**
     * 파트너별 매장 예약 목록 조회 API
     * 파트너가 소유한 매장들의 예약 목록을 조회
     *
     * @param partnerId 파트너 ID
     * @param storeId   매장 ID (선택적)
     * @param date      예약 날짜 (선택적)
     * @param status    예약 상태 (선택적)
     * @return 매장 예약 목록 응답
     */
    @GetMapping("/partner/{partnerId}")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<List<ReservationDto.ReservationInfoResponse>>> getPartnerReservations(
            @PathVariable Long partnerId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate date,
            @RequestParam(required = false) List<String> status
    ) {
        authenticationUtil.validatePartnerOwnership(partnerId);

        List<ReservationDto.ReservationInfoResponse> reservations =
                reservationService.getPartnerReservations(partnerId, storeId, date, status);

        return ResponseEntity.ok(ApiResponse.success("매장 예약 목록을 성공적으로 조회했습니다.", reservations));
    }

    /**
     * 예약 상세 정보 조회 API
     * 특정 예약의 상세 정보를 조회
     *
     * @param reservationId 예약 ID
     * @return 예약 상세 정보 응답
     */
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationDto.ReservationInfoResponse>> getReservationDetail(
            @PathVariable Long reservationId
    ) {
        ReservationDto.ReservationInfoResponse reservation = reservationService.getReservationDetail(reservationId);

        String currentUserEmail = authenticationUtil.getCurrentUserEmail();
        Long currentUserId = authenticationUtil.getCurrentUserId();

        boolean isReservationOwner = reservation.getUserId().equals(currentUserId);
        boolean isStorePartner = false;

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();
        boolean isPartner = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PARTNER"));

        if (isPartner) {
            Store store = storeRepository.findById(reservation.getStoreId())
                    .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
            isStorePartner = store.getPartner().getEmail().equals(currentUserEmail);
        }

        if (!isReservationOwner && !isStorePartner) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return ResponseEntity.ok(ApiResponse.success("예약 상세 정보를 성공적으로 조회했습니다.", reservation));
    }

    /**
     * 예약 취소 API
     * 사용자 또는 파트너(점장)가 예약을 취소
     *
     * @param reservationId 예약 ID
     * @return 취소된 예약 정보 응답
     */
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<ReservationDto.ReservationInfoResponse>> cancelReservation(
            @PathVariable Long reservationId
    ) {
        String currentUserEmail = authenticationUtil.getCurrentUserEmail();
        Long currentUserId = authenticationUtil.getCurrentUserId();

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();
        boolean isPartner = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PARTNER"));

        ReservationDto.ReservationInfoResponse response =
                reservationService.cancelReservation(reservationId, currentUserId, isPartner);

        return ResponseEntity.ok(ApiResponse.success("예약이 성공적으로 취소되었습니다.", response));
    }
}
