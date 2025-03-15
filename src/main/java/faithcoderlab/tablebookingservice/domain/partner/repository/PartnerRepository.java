package faithcoderlab.tablebookingservice.domain.partner.repository;

import faithcoderlab.tablebookingservice.domain.partner.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 파트너(점장) 레포지토리 인터페이스
 * 파트너 데이터 접근 인터페이스
 */
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    /**
     * 이메일로 파트너 찾기
     *
     * @param email 이메일
     * @return 파트너 Optional 객체
     */
    Optional<Partner> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 전화번호로 파트너 찾기
     *
     * @param phone 전화번호
     * @return 파트너 Optional 객체
     */
    Optional<Partner> findByPhone(String phone);

    /**
     * 전화번호 존재 여부 확인
     *
     * @param phone 전화번호
     * @return 존재 여부
     */
    boolean existsByPhone(String phone);

    /**
     * 사업자등록번호 존재 여부 확인
     *
     * @param businessNumber 사업자등록번호
     * @return 존재 여부
     */
    boolean existsByBusinessNumber(String businessNumber);
}
