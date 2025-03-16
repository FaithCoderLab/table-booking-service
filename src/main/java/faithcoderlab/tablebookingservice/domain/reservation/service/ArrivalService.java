package faithcoderlab.tablebookingservice.domain.reservation.service;

import faithcoderlab.tablebookingservice.domain.reservation.dto.ArrivalDto;
import faithcoderlab.tablebookingservice.domain.reservation.entity.Reservation;
import faithcoderlab.tablebookingservice.domain.reservation.entity.ReservationStatus;
import faithcoderlab.tablebookingservice.domain.reservation.repository.ReservationRepository;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * 도착 확인 서비스 클래스
 * 키오스크를 통한 도착 확인 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class ArrivalService {

    private final ReservationRepository reservationRepository;

    private static final int ARRIVAL_WINDOW_MINUTES = 10;

    /**
     * 도착 확인 처리 메서드
     * 예약된 시간 10분 전부터 도착 확인 가능
     *
     * @param request 도착 확인 요청 정보
     * @return 도착 확인 처리 결과
     */
    @Transactional
    public ArrivalDto.ArrivalResponse confirmArrival(ArrivalDto.ArrivalRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        validateReservationForArrival(reservation);

        LocalDateTime now = LocalDateTime.now();
        reservation.setArrivedAt(now);
        reservation.setStatus(ReservationStatus.ARRIVED);

        Reservation updatedReservation = reservationRepository.save(reservation);

        return ArrivalDto.ArrivalResponse.builder()
                .reservationId(updatedReservation.getId())
                .userName(updatedReservation.getUser().getName())
                .storeName(updatedReservation.getStore().getName())
                .arrivedAt(updatedReservation.getArrivedAt())
                .message("환영합니다! 도착 확인이 완료되었습니다.")
                .build();
    }

    /**
     * 도착 확인 가능 여부 검증 메서드
     * 예약 상태 및 시간 검증
     *
     * @param reservation 예약 객체
     */
    private void validateReservationForArrival(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            if (reservation.getStatus() == ReservationStatus.ARRIVED) {
                throw new CustomException(ErrorCode.INVALID_REQUEST, "이미 도착 확인이 완료된 예약입니다.");
            } else if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new CustomException(ErrorCode.INVALID_REQUEST, "취소된 예약입니다.");
            } else if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                throw new CustomException(ErrorCode.INVALID_REQUEST, "이미 완료된 예약입니다.");
            } else if (reservation.getStatus() == ReservationStatus.PENDING) {
                throw new CustomException(ErrorCode.INVALID_REQUEST, "아직 확정되지 않은 예약입니다.");
            } else {
                throw new CustomException(ErrorCode.INVALID_REQUEST, "도착 확인이 불가능한 상태의 예약입니다.");
            }
        }

        if (!reservation.getReservationDate().isEqual(LocalDateTime.now().toLocalDate())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "오늘 날짜의 예약만 도착 확인이 가능합니다.");
        }

        LocalTime now = LocalTime.now();

        LocalTime reservationTime = reservation.getReservationTime();

        LocalTime arrivalWindowStart = reservationTime.minus(ARRIVAL_WINDOW_MINUTES, ChronoUnit.MINUTES);

        LocalTime arrivalWindowEnd = reservationTime.plus(30, ChronoUnit.MINUTES);

        if (now.isBefore(arrivalWindowStart)) {
            throw new CustomException(
                    ErrorCode.INVALID_RESERVATION_TIME,
                    String.format("아직 도착 확인 가능 시간이 아닙니다. %d분 후부터 도착 확인이 가능합니다.",
                            ChronoUnit.MINUTES.between(now, arrivalWindowStart))
            );
        }

        if (now.isAfter(arrivalWindowEnd)) {
            throw new CustomException(
                    ErrorCode.INVALID_RESERVATION_TIME,
                    "예약 시간이 지나 도착 확인이 불가능합니다. 매장에 문의해주세요."
            );
        }
    }
}
