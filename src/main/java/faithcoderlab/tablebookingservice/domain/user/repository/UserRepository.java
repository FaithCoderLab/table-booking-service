package faithcoderlab.tablebookingservice.domain.user.repository;

import faithcoderlab.tablebookingservice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 일반 사용자 레포지토리 인터페이스
 * 사용자 데이터 접근 인터페이스
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 찾기
     *
     * @param email 이메일
     * @return 사용자 Optional 객체
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 전화번호로 사용자 찾기
     *
     * @param phone 전화번호
     * @return 사용자 Optional 객체
     */
    Optional<User> findByPhone(String phone);

    /**
     * 전화번호 존재 여부 확인
     *
     * @param phone 전화번호
     * @return 존재 여부
     */
    boolean existsByPhone(String phone);
}
