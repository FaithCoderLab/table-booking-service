package faithcoderlab.tablebookingservice.global.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 유틸리티 클래스
 * JWT 토큰 생성 및 검증을 위한 유틸리티 메서드 제공
 */
@Component
public class JwtUtil {

    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    @Value("${spring.jwt.secret}")
    private String secretFromEnv;

    private Key key;

    /**
     * 초기화 메서드
     * application.yml에서 가져온 시크릿 키를 사용하여 Key 객체 생성
     */
    @PostConstruct
    public void init() {
        if (secretFromEnv == null || secretFromEnv.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret key is not configured. Please set jwt.secret in application.yml or JWT_SECRET environment variable.");
        }
        this.key = Keys.hmacShaKeyFor(secretFromEnv.getBytes());
    }

    /**
     * 토큰에서 이메일 추출
     *
     * @param token JWT 토큰
     * @return 이메일
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 토큰에서 만료 시간 추출
     *
     * @param token JWT 토큰
     * @return 만료 시간
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 클레임 추출
     *
     * @param token JWT 토큰
     * @param claimsResolver 클레임 리졸버 함수
     * @return 추출된 클레임 값
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임 추출
     *
     * @param token JWT 토큰
     * @return 모든 클레임
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * 토큰 만료 여부 확인
     *
     * @param token JWT 토큰
     * @return 만료 여부
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 이메일을 기반으로
     *
     * @param email 사용자 이메일
     * @param role  사용자 역할
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email);
    }

    /**
     * 클레임과 주체(이메일)를 기반으로 JWT 토큰 생성
     *
     * @param claims  클레임 맵
     * @param subject 토큰 주체(이메일)
     * @return 생성된 JWT 토큰
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token JWT 토큰
     * @param userEmail 사용자 이메일
     * @return 유효 여부
     */
    public Boolean validateToken(String token, String userEmail) {
        final String email = extractEmail(token);
        return (email.equals(userEmail) && !isTokenExpired(token));
    }
}
