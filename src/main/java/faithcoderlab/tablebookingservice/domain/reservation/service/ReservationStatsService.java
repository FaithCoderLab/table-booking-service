package faithcoderlab.tablebookingservice.domain.reservation.service;

import faithcoderlab.tablebookingservice.domain.reservation.dto.ReservationStatsDto;
import faithcoderlab.tablebookingservice.domain.reservation.entity.Reservation;
import faithcoderlab.tablebookingservice.domain.reservation.entity.ReservationStatus;
import faithcoderlab.tablebookingservice.domain.reservation.repository.ReservationRepository;
import faithcoderlab.tablebookingservice.domain.store.entity.Store;
import faithcoderlab.tablebookingservice.domain.store.repository.StoreRepository;
import faithcoderlab.tablebookingservice.global.exception.CustomException;
import faithcoderlab.tablebookingservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 예약 통계 서비스 클래스
 * 예약 데이터를 분석하여 다양한 통계 정보를 제공
 */
@Service
@RequiredArgsConstructor
public class ReservationStatsService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    /**
     * 기간별 예약 통계 계산 메서드
     *
     * @param storeId   매장 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 기간별 예약 통계 정보
     */
    @Transactional(readOnly = true)
    public ReservationStatsDto.PeriodStatsResponse getPeriodStats(Long storeId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        Store store = getStoreById(storeId);
        List<Reservation> reservations = getReservationsForPeriod(storeId, startDate, endDate);

        long totalReservations = reservations.size();

        long confirmedReservations = countByStatus(reservations, ReservationStatus.CONFIRMED, ReservationStatus.ARRIVED, ReservationStatus.COMPLETED);
        long cancelledReservations = countByStatus(reservations, ReservationStatus.CANCELLED, ReservationStatus.REJECTED);
        long noShowReservations = countByStatus(reservations, ReservationStatus.NO_SHOW);

        double averagePartySize = reservations.stream()
                .mapToInt(Reservation::getPartySize)
                .average()
                .orElse(0);

        Map<String, Long> dailyReservationCounts = reservations.stream()
                .collect(Collectors.groupingBy(
                        reservation -> reservation.getReservationDate().format(DateTimeFormatter.ISO_DATE),
                        Collectors.counting()
                ));

        return ReservationStatsDto.PeriodStatsResponse.builder()
                .storeId(storeId)
                .storeName(store.getName())
                .startDate(startDate)
                .endDate(endDate)
                .totalReservations(totalReservations)
                .confirmedReservations(confirmedReservations)
                .cancelledReservations(cancelledReservations)
                .noShowReservations(noShowReservations)
                .averagePartySize(averagePartySize)
                .dailyReservationCounts(dailyReservationCounts)
                .build();
    }

    /**
     * 시간대별 예약 통계 계산 메서드
     *
     * @param storeId   매장 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 시간대별 예약 통계 정보
     */
    @Transactional(readOnly = true)
    public ReservationStatsDto.TimeSlotStatsResponse getTimeSlotStats(Long storeId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        Store store = getStoreById(storeId);
        List<Reservation> reservations = getReservationsForPeriod(storeId, startDate, endDate);

        Map<String, Long> timeSlotDistribution = reservations.stream()
                .collect(Collectors.groupingBy(
                        reservation -> reservation.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        Collectors.counting()
                ));

        List<ReservationStatsDto.TimeSlotData> mostPopularTimeSlots = timeSlotDistribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> ReservationStatsDto.TimeSlotData.builder()
                        .timeSlot(LocalTime.parse(entry.getKey(), DateTimeFormatter.ofPattern("HH:mm")))
                        .count(entry.getValue())
                        .build())
                .toList();

        return ReservationStatsDto.TimeSlotStatsResponse.builder()
                .storeId(storeId)
                .storeName(store.getName())
                .startDate(startDate)
                .endDate(endDate)
                .timeSlotDistribution(timeSlotDistribution)
                .mostPopularTimeSlots(mostPopularTimeSlots)
                .build();
    }

    /**
     * 상태별 예약 통계 계산 메서드
     *
     * @param storeId   매장 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 상태별 예약 통계 정보
     */
    @Transactional(readOnly = true)
    public ReservationStatsDto.StatusStatsResponse getStatusStats(Long storeId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        Store store = getStoreById(storeId);
        List<Reservation> reservations = getReservationsForPeriod(storeId, startDate, endDate);

        long totalReservations = reservations.size();
        if (totalReservations == 0) {
            return createEmptyStatusStats(store, startDate, endDate);
        }

        Map<String, Long> statusDistribution = reservations.stream()
                .collect(Collectors.groupingBy(
                        reservation -> reservation.getStatus().name(),
                        Collectors.counting()
                ));

        double confirmationRate = calculateRate(reservations, totalReservations,
                ReservationStatus.CONFIRMED, ReservationStatus.ARRIVED, ReservationStatus.COMPLETED);

        double cancellationRate = calculateRate(reservations, totalReservations,
                ReservationStatus.CANCELLED, ReservationStatus.REJECTED);

        double noShowRate = calculateRate(reservations, totalReservations,
                ReservationStatus.NO_SHOW);

        return ReservationStatsDto.StatusStatsResponse.builder()
                .storeId(storeId)
                .storeName(store.getName())
                .startDate(startDate)
                .endDate(endDate)
                .statusDistribution(statusDistribution)
                .confirmationRate(confirmationRate)
                .cancellationRate(cancellationRate)
                .noShowRate(noShowRate)
                .build();
    }

    /**
     * 매장 ID로 매장 정보 조회
     *
     * @param storeId 매장 ID
     * @return 매장 정보
     * @throws CustomException 매장을 찾을 수 없을 경우
     */
    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
    }

    /**
     * 특정 기간의 매장 예약 목록 조회
     *
     * @param storeId   매장 ID
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 예약 목록
     */
    private List<Reservation> getReservationsForPeriod(Long storeId, LocalDate startDate, LocalDate endDate) {
       return reservationRepository.findByStoreIdAndReservationDateBetween(storeId, startDate, endDate);
    }

    /**
     * 날짜 범위 유효성 검증
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @throws CustomException 날짜 범위가 유효하지 않을 경우
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "시작 날짜와 종료 날짜는 필수 입력 항목입니다.");
        }

        if (startDate.isAfter(endDate)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        if (endDate.isAfter(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "종료 날짜는 현재 날짜보다 이후일 수 없습니다.");
        }

        if (startDate.isBefore(LocalDate.now().minusYears(1))) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "최대 1년 전 데이터까지만 조회 가능합니다.");
        }
    }

    /**
     * 특정 상태의 예약 수 카운트
     *
     * @param reservations 예약 목록
     * @param statuses     카운트할 예약 상태 목록
     * @return 해당 상태의 예약 수
     */
    private long countByStatus(List<Reservation> reservations, ReservationStatus... statuses) {
        List<ReservationStatus> statusList = Arrays.asList(statuses);
        return reservations.stream()
                .filter(reservation -> statusList.contains(reservation.getStatus()))
                .count();
    }

    /**
     * 특정 상태의 예약 비율 계산
     *
     * @param reservations      예약 목록
     * @param totalReservations 총 예약 수
     * @param statuses          계산할 예약 상태 목록
     * @return 해당 상태의 예약 비율
     */
    private double calculateRate(List<Reservation> reservations, long totalReservations, ReservationStatus... statuses) {
        if (totalReservations == 0) {
            return 0.0;
        }

        long count = countByStatus(reservations, statuses);
        return (double) count / totalReservations * 100;
    }

    /**
     * 빈 상태 통계 객체 생성
     * @param store 매장 정보
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 빈 상태 통계 객체
     */
    private ReservationStatsDto.StatusStatsResponse createEmptyStatusStats(Store store, LocalDate startDate, LocalDate endDate) {
        return ReservationStatsDto.StatusStatsResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .startDate(startDate)
                .endDate(endDate)
                .statusDistribution(new HashMap<>())
                .confirmationRate(0.0)
                .cancellationRate(0.0)
                .noShowRate(0.0)
                .build();
    }
}
