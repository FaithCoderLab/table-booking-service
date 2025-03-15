package faithcoderlab.tablebookingservice.domain.reservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 예약 설정 클래스
 * 예약 관련 설정값을 관리
 */
@Component
public class ReservationConfig {

    /**
     * 운영 시작 시간 (기본값: 오전 9시)
     */
    @Value("${reservation.operation.start-time:09:00}")
    private String operationStartTimeStr;

    /**
     * 운영 종료 시간 (기본값: 오후 10시)
     */
    @Value("${reservation.operation.end-time:22:00}")
    private String operationEndTimeStr;

    /**
     * 예약 간격 (분 단위, 기본값: 30분)
     */
    @Value("${reservation.interval-minutes:30}")
    private int intervalMinutes;

    /**
     * 예약 가능 기간 (일 단위, 기본값: 14일)
     */
    @Value("${reservation.available-days-ahead:14}")
    private int availableDaysAhead;

    /**
     * 예약 가능한 모든 시간 목록 반환
     *
     * @return 모든 예약 가능 시간 목록
     */
    public List<LocalTime> getAllAvailableTimes() {
        LocalTime startTime = LocalTime.parse(operationStartTimeStr);
        LocalTime endTime = LocalTime.parse(operationEndTimeStr);

        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime currentTime = startTime;

        while (currentTime.isBefore(endTime)) {
            timeSlots.add(currentTime);
            currentTime = currentTime.plus(intervalMinutes, ChronoUnit.MINUTES);
        }

        return timeSlots;
    }

    /**
     * 운영 시작 시간 조회
     *
     * @return 운영 시작 시간
     */
    public LocalTime getOperationStartTime() {
        return LocalTime.parse(operationStartTimeStr);
    }

    /**
     * 운영 종료 시간 조회
     *
     * @return 운영 종료 시간
     */
    public LocalTime getOperationEndTime() {
        return LocalTime.parse(operationEndTimeStr);
    }

    /**
     * 예약 간격(분) 조회
     *
     * @return 예약 간격(분)
     */
    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    /**
     * 예약 가능 기간(일) 조회
     *
     * @return 예약 가능 기간(일)
     */
    public int getAvailableDaysAhead() {
        return availableDaysAhead;
    }
}
