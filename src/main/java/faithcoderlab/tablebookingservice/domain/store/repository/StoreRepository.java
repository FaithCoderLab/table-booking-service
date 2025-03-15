package faithcoderlab.tablebookingservice.domain.store.repository;

import faithcoderlab.tablebookingservice.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 매장 레포지토리 인터페이스
 * 매장 데이터 접근 인터페이스
 */
public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * 매장 이름으로 매장 찾기
     *
     * @param name 매장 이름
     * @return 매장 Optional 객체
     */
    Optional<Store> findByName(String name);

    /**
     * 매장 이름 존재 여부 확인
     *
     * @param name 매장 이름
     * @return 존재 여부
     */
    boolean existsByName(String name);

    /**
     * 파트너 ID로 매장 목록 찾기
     *
     * @param partnerId 파트너 ID
     * @return 매장 목록
     */
    List<Store> findByPartnerId(Long partnerId);

    List<Store> findAllByActiveOrderByNameAsc(boolean active);

    // TODO: rating에 따른 정렬
    List<Store> findAllByActive(boolean active);
}
