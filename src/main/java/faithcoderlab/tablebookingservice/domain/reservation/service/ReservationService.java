package faithcoderlab.tablebookingservice.domain.reservation.service;

import faithcoderlab.tablebookingservice.domain.reservation.config.ReservationConfig;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
     * @param userId 사용자 ID
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
     * @param date 예약 날짜
     * @param time 예약 시간
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
}
