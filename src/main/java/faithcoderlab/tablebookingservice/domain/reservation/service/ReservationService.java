package faithcoderlab.tablebookingservice.domain.reservation.service;

import faithcoderlab.tablebookingservice.domain.notification.service.NotificationService;
import faithcoderlab.tablebookingservice.domain.partner.repository.PartnerRepository;
import faithcoderlab.tablebookingservice.domain.reservation.config.ReservationConfig;
import faithcoderlab.tablebookingservice.domain.reservation.dto.ReservationApprovalDto;
import faithcoderlab.tablebookingservice.domain.reservation.dto.ReservationDto;
import faithcoderlab.tablebookingservice.domain.reservation.entity.Reservation;
import faithcoderlab.tablebookingservice.domain.reservation.entity.ReservationStatus;
import faithcoderlab.tablebookingservice.domain.reservation.repository.ReservationRepository;
import faithcoderlab.tablebookingservice.domain.store.entity.Store;
import faithcoderlab.tablebookingservice.domain.store.repository.StoreRepository;
import faithcoderlab.tablebookingservice.domain.user.entity.User;
import faithcoderlab.tablebookingservice.domain.user.repository.UserRepository;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 예약 서비스 클래스
 * 예약 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ReservationConfig reservationConfig;
    private final PartnerRepository partnerRepository;
    private final NotificationService notificationService;

    /**
     * 예약 가능 시간 조회 메서드
     *
     * @param request 예약 가능 시간 조회 요청 정보
     * @return 예약 가능 시간 목록
     */
    @Transactional(readOnly = true)
    public ReservationDto.AvailableTimesResponse getAvailableTimes(ReservationDto.AvailableTimesRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!store.isActive()) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        LocalDate today = LocalDate.now();
        if (request.getDate().isBefore(today)) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME, "과거 날짜에는 예약할 수 없습니다.");
        }

        LocalDate maxDate = today.plusDays(reservationConfig.getAvailableDaysAhead());
        if (request.getDate().isAfter(maxDate)) {
            throw new CustomException(
                    ErrorCode.INVALID_RESERVATION_TIME,
                    String.format("현재 날짜로부터 최대 %d일 후까지만 예약할 수 있습니다.", reservationConfig.getAvailableDaysAhead())
            );
        }

        List<ReservationStatus> activeStatuses = Arrays.asList(
                ReservationStatus.PENDING,
                ReservationStatus.CONFIRMED
        );

        List<Reservation> existingReservations = reservationRepository.findByStoreIdAndReservationDateAndStatusIn(
                request.getStoreId(),
                request.getDate(),
                activeStatuses
        );

        List<LocalTime> bookedTimes = existingReservations.stream()
                .map(Reservation::getReservationTime)
                .collect(Collectors.toList());

        List<LocalTime> allTimeSlots = reservationConfig.getAllAvailableTimes();

        LocalTime currentTime = LocalTime.now();

        List<LocalTime> availableTimes = allTimeSlots.stream()
                .filter(time -> !bookedTimes.contains(time))
                .filter(time -> !request.getDate().equals(today) || time.isAfter(currentTime))
                .collect(Collectors.toList());

        return ReservationDto.AvailableTimesResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .date(request.getDate())
                .availableTimes(availableTimes)
                .build();
    }

    /**
     * 예약 생성 메서드
     *
     * @param userId  사용자 ID
     * @param request 예약 생성 요청 정보
     * @return 생성된 예약 정보
     */
    @Transactional
    public ReservationDto.CreateResponse createReservation(Long userId, ReservationDto.CreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!store.isActive()) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        validateReservationDateTime(request.getReservationDate(), request.getReservationTime());

        checkTimeAvailability(request.getStoreId(), request.getReservationDate(), request.getReservationTime());

        Reservation reservation = Reservation.builder()
                .user(user)
                .store(store)
                .reservationDate(request.getReservationDate())
                .reservationTime(request.getReservationTime())
                .partySize(request.getPartySize())
                .status(ReservationStatus.PENDING)
                .specialRequests(request.getSpecialRequests())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationDto.CreateResponse.builder()
                .reservationId(savedReservation.getId())
                .storeId(store.getId())
                .storeName(store.getName())
                .userId(user.getId())
                .userName(user.getName())
                .reservationDate(savedReservation.getReservationDate())
                .reservationTime(savedReservation.getReservationTime())
                .partySize(savedReservation.getPartySize())
                .status(savedReservation.getStatus())
                .specialRequests(savedReservation.getSpecialRequests())
                .createdAt(savedReservation.getCreatedAt())
                .build();
    }

    /**
     * 예약 시간 가용성 검증 메서드
     *
     * @param storeId 매장 ID
     * @param date    예약 날짜
     * @param time    예약 시간
     */
    private void checkTimeAvailability(Long storeId, LocalDate date, LocalTime time) {
        List<ReservationStatus> activeStatuses = Arrays.asList(
                ReservationStatus.PENDING,
                ReservationStatus.CONFIRMED
        );

        boolean isTimeBooked = reservationRepository.existsByStoreIdAndReservationDateAndReservationTimeAndStatusIn(
                storeId,
                date,
                time,
                activeStatuses
        );

        if (isTimeBooked) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }

    /**
     * 예약 날짜 및 시간 유효성 검증 메서드
     *
     * @param date 예약 날짜
     * @param time 예약 시간
     */
    private void validateReservationDateTime(LocalDate date, LocalTime time) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (date.isBefore(today)) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME, "과거 날짜에는 예약할 수 없습니다.");
        }

        if (date.equals(today) && time.equals(now)) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME, "현재 시간 이후로만 예약할 수 있습니다.");
        }

        LocalDate maxDate = today.plusDays(reservationConfig.getAvailableDaysAhead());
        if (date.isAfter(maxDate)) {
            throw new CustomException(
                    ErrorCode.INVALID_RESERVATION_TIME,
                    String.format("현재 날짜로부터 최대 %d일 후까지만 예약할 수 있습니다.", reservationConfig.getAvailableDaysAhead()
                    ));
        }

        LocalTime startTime = reservationConfig.getOperationStartTime();
        LocalTime endTime = reservationConfig.getOperationEndTime();

        if (time.isBefore(startTime) || time.isAfter(endTime)) {
            throw new CustomException(
                    ErrorCode.INVALID_RESERVATION_TIME,
                    String.format("예약 가능 시간은 %s부터 %s까지입니다.", startTime, endTime)
            );
        }

        int minutes = time.getMinute();
        if (minutes % reservationConfig.getIntervalMinutes() != 0) {
            throw new CustomException(
                    ErrorCode.INVALID_RESERVATION_TIME,
                    String.format("예약은 %d분 단위로만 가능합니다.", reservationConfig.getIntervalMinutes())
            );
        }
    }

    /**
     * 사용자별 예약 목록 조회 메서드
     *
     * @param userId     사용자 ID
     * @param statusList 조회할 상태 목록 (선택적)
     * @return 사용자의 예약 목록
     */
    @Transactional(readOnly = true)
    public List<ReservationDto.ReservationInfoResponse> getUserReservations(Long userId, List<String> statusList) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<ReservationStatus> statuses = getReservationStatuses(statusList);

        List<Reservation> reservations;
        if (statuses.isEmpty()) {
            reservations = reservationRepository.findByUserIdOrderByReservationDateDescReservationTimeDesc(userId);
        } else {
            reservations = reservationRepository.findByUserIdAndStatusInOrderByReservationDateDescReservationTimeDesc(
                    userId, statuses
            );
        }

        return reservations.stream()
                .map(this::converToReservationInfoResponse)
                .collect(Collectors.toList());
    }

    /**
     * 파트너별 매장 예약 목록 조회 메서드
     *
     * @param partnerId  파트너 ID
     * @param storeId    매장 ID (선택적)
     * @param date       예약 날짜 (선택적)
     * @param statusList 조회할 상태 목록 (선택적)
     * @return 매장 예약 목록
     */
    @Transactional(readOnly = true)
    public List<ReservationDto.ReservationInfoResponse> getPartnerReservations(
            Long partnerId, Long storeId, LocalDate date, List<String> statusList
    ) {
        if (!partnerRepository.existsById(partnerId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<ReservationStatus> statuses = getReservationStatuses(statusList);
        List<Reservation> reservations;

        if (storeId != null) {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

            if (!store.getPartner().getId().equals(partnerId)) {
                throw new CustomException(ErrorCode.FORBIDDEN, "해당 매장에 대한 접근 권한이 없습니다.");
            }

            if (date != null) {
                if (statuses.isEmpty()) {
                    reservations = reservationRepository.findByStoreIdAndReservationDateOrderByReservationTime(
                            storeId, date
                    );
                } else {
                    reservations = reservationRepository.findByStoreIdAndReservationDateAndStatusInOrderByReservationTime(
                            storeId, date, statuses
                    );
                }
            } else {
                if (statuses.isEmpty()) {
                    reservations = reservationRepository.findByStoreIdOrderByReservationDateDescReservationTimeDesc(
                            storeId
                    );
                } else {
                    reservations = reservationRepository.findByStoreIdAndStatusInOrderByReservationDateDescReservationTimeDesc(
                            storeId, statuses
                    );
                }
            }
        } else {
            List<Long> storeIds = storeRepository.findByPartnerId(partnerId).stream()
                    .map(Store::getId)
                    .collect(Collectors.toList());

            if (date != null) {
                if (statuses.isEmpty()) {
                    reservations = reservationRepository.findByStoreIdInAndReservationDateOrderByStoreIdAscReservationTime(
                            storeIds, date
                    );
                } else {
                    reservations = reservationRepository.findByStoreIdInAndReservationDateAndStatusInOrderByStoreIdAscReservationTime(
                            storeIds, date, statuses
                    );
                }
            } else {
                if (statuses.isEmpty()) {
                    reservations = reservationRepository.findByStoreIdInOrderByReservationDateDescReservationTimeDesc(
                            storeIds
                    );
                } else {
                    reservations = reservationRepository.findByStoreIdInAndStatusInOrderByReservationDateDescReservationTimeDesc(
                            storeIds, statuses
                    );
                }
            }
        }

        return reservations.stream()
                .map(this::converToReservationInfoResponse)
                .collect(Collectors.toList());
    }

    /**
     * 문자열 상태 목록을 ReservationStatus 열거형 목록으로 변환
     *
     * @param statusList 문자열 상태 목록
     * @return ReservationStatus 목록
     */
    private List<ReservationStatus> getReservationStatuses(List<String> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return new ArrayList<>();
        }

        return statusList.stream()
                .map(status -> {
                    try {
                        return ReservationStatus.valueOf(status.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new CustomException(ErrorCode.INVALID_REQUEST, "유효하지 않은 예약 상태: " + status);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Reservation 엔티티를 ReservationInfoResponse DTO로 변환
     *
     * @param reservation Reservation 엔티티
     * @return ReservationInfoResponse DTO
     */
    private ReservationDto.ReservationInfoResponse converToReservationInfoResponse(Reservation reservation) {
        return ReservationDto.ReservationInfoResponse.builder()
                .reservationId(reservation.getId())
                .storeId(reservation.getStore().getId())
                .storeName(reservation.getStore().getName())
                .userId(reservation.getUser().getId())
                .userName(reservation.getUser().getName())
                .userPhone(reservation.getUser().getPhone())
                .reservationDate(reservation.getReservationDate())
                .reservationTime(reservation.getReservationTime())
                .partySize(reservation.getPartySize())
                .status(reservation.getStatus())
                .arrivedAt(reservation.getArrivedAt())
                .completedAt(reservation.getCompletedAt())
                .specialRequests(reservation.getSpecialRequests())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    /**
     * 예약 상세 정보 조회 메서드
     *
     * @param reservationId 예약 ID
     * @return 예약 상세 정보
     */
    @Transactional(readOnly = true)
    public ReservationDto.ReservationInfoResponse getReservationDetail(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return converToReservationInfoResponse(reservation);
    }

    /**
     * 예약 취소 메서드
     *
     * @param reservationId 예약 ID
     * @param actorId       액터 ID (사용자 또는 파트너)
     * @param isPartner     파트너 여부
     * @return 취소된 예약 정보
     */
    @Transactional
    public ReservationDto.ReservationInfoResponse cancelReservation(Long reservationId, Long actorId, boolean isPartner) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        validateCancellationPermission(reservation, actorId, isPartner);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "이미 취소된 예약입니다.");
        }

        if (reservation.getStatus() == ReservationStatus.ARRIVED ||
                reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_ALLOWED, "이미 방문 확인되었거나 완료된 예약은 취소할 수 없습니다.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation cancelledReservation = reservationRepository.save(reservation);

        return converToReservationInfoResponse(cancelledReservation);
    }

    /**
     * 예약 취소 권한 검증 메서드
     *
     * @param reservation 예약 객체
     * @param actorId     액터 ID (사용자 또는 파트너)
     * @param isPartner   파트너 여부
     */
    private void validateCancellationPermission(Reservation reservation, Long actorId, boolean isPartner) {
        if (isPartner) {
            if (!reservation.getStore().getPartner().getId().equals(actorId)) {
                throw new CustomException(ErrorCode.FORBIDDEN, "해당 매장의 예약을 취소할 권한이 없습니다.");
            }
        } else {
            if (!reservation.getUser().getId().equals(actorId)) {
                throw new CustomException(ErrorCode.FORBIDDEN, "본인의 예약만 취소할 수 있습니다.");
            }
        }
    }

    /**
     * 예약 완료 처리 메서드
     * 파트너(점장)가 서비스 완료 후 예약 상태를 COMPLETED로 변경
     *
     * @param reservationId 예약 ID
     * @param partnerId     파트너 ID
     * @return 완료 처리된 예약 정보
     */
    @Transactional
    public ReservationDto.ReservationInfoResponse completeReservation(Long reservationId, Long partnerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getStore().getPartner().getId().equals(partnerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "해당 매장의 예약을 처리할 권한이 없습니다.");
        }

        if (reservation.getStatus() != ReservationStatus.ARRIVED) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "도착 확인된 예약만 완료 처리할 수 있습니다.");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setCompletedAt(LocalDateTime.now());

        Reservation completedReservation = reservationRepository.save(reservation);

        return converToReservationInfoResponse(completedReservation);
    }

    /**
     * 예약 승인/거절 처리 메서드
     * 파트너(점장)가 예약 요청을 승인하거나 거절
     *
     * @param reservationId 예약 ID
     * @param partnerId     파트너 ID
     * @param request       승인/거절 요청 정보
     * @return 승인/거절 처리 결과
     */
    @Transactional
    public ReservationApprovalDto.ApprovalResponse processReservationApproval(
            Long reservationId, Long partnerId, ReservationApprovalDto.@Valid ApprovalRequest request
    ) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getStore().getPartner().getId().equals(partnerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "해당 매장의 예약을 처리할 권한이 없습니다.");
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "대기 중인 예약만 승인/거절할 수 있습니다.");
        }

        String message;

        if (request.getApproved()) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            message = "예약이 승인되었습니다.";
        } else {
            reservation.setStatus(ReservationStatus.REJECTED);
            message = "예약이 거절되었습니다.";

            if (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty()) {
                request.setRejectionReason("매장 사정으로 인해 예약이 거절되었습니다.");
            }
        }

        Reservation processedReservation = reservationRepository.save(reservation);

        notificationService.createReservationStatusNotification(
                processedReservation.getUser().getId(),
                processedReservation.getId(),
                processedReservation.getStore().getName(),
                request.getApproved(),
                message,
                request.getRejectionReason()
        );

        return ReservationApprovalDto.ApprovalResponse.builder()
                .reservationId(processedReservation.getId())
                .storeName(processedReservation.getStore().getName())
                .userName(processedReservation.getUser().getName())
                .approved(request.getApproved())
                .message(message)
                .rejectionReason(request.getApproved() ? null : request.getRejectionReason())
                .build();
    }

}
