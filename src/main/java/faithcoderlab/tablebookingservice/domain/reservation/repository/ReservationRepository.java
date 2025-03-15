package faithcoderlab.tablebookingservice.domain.reservation.repository;

import faithcoderlab.tablebookingservice.domain.reservation.entity.Reservation;
import faithcoderlab.tablebookingservice.domain.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 예약 레포지토리 인터페이스
 * 예약 데이터 접근 인터페이스
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 매장 ID와 예약 날짜로 예약 목록을 조회
     *
     * @param storeId 매장 ID
     * @param date 예약 날짜
     * @return 예약 목록
     */
    List<Reservation> findByStoreIdAndReservationDate(Long storeId, LocalDate date);

    /**
     * 매장 ID, 예약 날짜, 상태 목록으로 예약 목록 조회
     *
     * @param storeId 매장 ID
     * @param date 예약 날짜
     * @param statuses 예약 상태 목록
     * @return 예약 목록
     */
    List<Reservation> findByStoreIdAndReservationDateAndStatusIn(
            Long storeId, LocalDate date, List<ReservationStatus> statuses
    );

    /**
     * 매장 ID, 예약 날짜, 예약 시간으로 예약 존재 여부 확인
     *
     * @param storeId 매장 ID
     * @param date 예약 날짜
     * @param time 예약 시간
     * @param statuses 예약 상태 목록
     * @return 예약 존재 여부
     */
    boolean existsByStoreIdAndReservationDateAndReservationTimeAndStatusIn(
            Long storeId, LocalDate date, LocalTime time, List<ReservationStatus> statuses
    );

    /**
     * 사용자 ID와 상태 목록으로 예약 목록 조회
     *
     * @param userId   사용자 ID
     * @param statuses 예약 상태 목록
     * @return 예약 목록
     */
    List<Reservation> findByUserIdAndStatusInOrderByReservationDateDescReservationTimeDesc(
            Long userId, List<ReservationStatus> statuses
    );


}
