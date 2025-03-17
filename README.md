# 매장 테이블 예약 서비스

Spring Boot 기반 매장 테이블 예약 서비스 백엔드 API 프로젝트입니다.

## 프로젝트 소개
이 프로젝트는 "식당이나 점포를 이용하기 전에, 미리 예약하여 편하게 식당/점포를 이용할 수 있는 서비스" 개발을 목표로 했습니다.

## 주요 기능
- 파트너(매장 점장) 회원가입 및 관리
- 일반 사용자 회원가입 및 관리
- 매장 등록/수정/삭제/조회
- 예약 생성 및 관리
- 예약 승인/거절 기능
- 매장 방문 시 키오스크를 통한 도착 확인
- 예약 후 리뷰 작성 기능
- 알림 기능
- 예약 통계 기능

## 기술 스택
- Java 17
- Spring Boot 3.4.3
- Spring Security
- Spring Data JPA
- MySQL/MariaDB
- Gradle
- JWT Authentication
- Docker & Docker Compose

## 시스템 아키텍처
프로젝트는 다음과 같은 구조를 가집니다:
- **Controller**: 클라이언트 요청 처리 및 응답 관리
- **Service**: 비즈니스 로직 처리
- **Repository**: 데이터 접근 계층
- **Entity**: 데이터 모델
- **DTO**: 데이터 전송 객체

### ERD 다이어그램
![erd](https://github.com/user-attachments/assets/0686de1d-e425-4a51-afc5-e8d9209d78aa)

## 시작하기
### 사전 요구사항
- Java 17 이상
- Docker 및 Docker Compose (선택 사항)
- MySQL/MariaDB (Docker 미사용 시)

### 설치 및 실행
#### 프로젝트 복제
```
git clone https://github.com/faithcoderlab/table-booking-service.git
cd table-booking-service
```

#### Docker를 사용한 데이터베이스 실행 (선택 사항)
```
docker-compose up -d
```

#### 애플리케이션 실행
```
./gradlew bootRun
```

또는
```
./gradlew build
java -jar build/libs/table-booking-service-0.0.1-SNAPSHOT.jar
```

### 환경 설정
`application.yml` 파일에서 다음과 같은 설정을 확인하거나 변경할 수 있습니다:
- 데이터베이스 연결 정보
- JWT 설정
- 서버 포트
- 예약 설정 (운영 시간, 예약 간격 등)

## API 문서
주요 API 엔드포인트는 다음과 같습니다:
### 인증 API
- `POST /api/auth/login`: 로그인

### 사용자 API
- `POST /api/users/signup`: 일반 사용자 회원가입
- `GET /api/users/{userId}`: 사용자 정보 조회
- `PUT /api/users/{userId}`: 사용자 정보 수정

### 파트너 API
- `POST /api/partners/signup`: 파트너 회원가입
- `GET /api/partners/{partnerId}`: 파트너 정보 조회
- `PUT /api/partners/{partnerId}`: 파트너 정보 수정

### 매장 API
- `POST /api/stores/partners/{partnerId}`: 매장 등록
- `GET /api/stores/partners/{partnerId}`: 파트너별 매장 목록 조회
- `GET /api/stores/{storeId}`: 매장 상세 정보 조회
- `PUT /api/stores/{storeId}/partners/{partnerId}`: 매장 정보 수정
- `DELETE /api/stores/{storeId}/partners/{partnerId}`: 매장 삭제
- `GET /api/stores`: 매장 목록 조회 (정렬 기준 적용)
- `GET /api/stores/recommendations`: 인기 매장 추천

### 예약 API
- `POST /api/reservations/available-times`: 예약 가능 시간 조회
- `POST /api/reservations`: 예약 생성
- `GET /api/reservations/user`: 사용자별 예약 목록 조회
- `GET /api/reservations/partner/{partnerId}`: 파트너별 매장 예약 목록 조회
- `GET /api/reservations/{reservationId}`: 예약 상세 정보 조회
- `PATCH /api/reservations/{reservationId}/cancel`: 예약 취소
- `PATCH /api/reservations/{reservationId}/approval/partners/{partnerId}`: 예약 승인/거절

### 키오스크 API
- `POST /api/kiosk/arrival`: 도착 확인

### 리뷰 API
- `POST /api/reviews`: 리뷰 생성
- `GET /api/reviews/stores/{storeId}`: 매장별 리뷰 목록 조회
- `GET /api/reviews/user`: 사용자 작성 리뷰 목록 조회
- `GET /api/reviews/{reviewId}`: 리뷰 상세 조회
- `PUT /api/reivews/{reviewId}`: 리뷰 수정
- `DELETE /api/reviews/{reviewId}`: 리뷰 삭제

### 알림 API
- `GET /api/notifications`: 사용자 알림 목록 조회
- `PATCH /api/notifications/{notificationId}/read`: 알림 읽음 처리

### 통계 API
- `GET /api/stats/reservations/period/stores/{storeId}/partners/{partnerId}`: 기간별 예약 통계
- `GET /api/stats/reservations/timeslot/stores/{storeId}/partners/{partnerId}`: 시간대별 예약 통계
- `GET /api/stats/reservations/status/stores/{storeId}/partners/{partnerId}`: 상태별 예약 통계

## 주요 기능 설명
### 회원 관리
- 일반 사용자와 매장 파트너(점장)를 위한 별도 회원 관리
- JWT 기반 인증 시스템

### 매장 관리
- 파트너는 자신의 매장 정보를 등록, 수정, 삭제 가능
- 사용자는 다양한 기준(가나다순, 평점순, 거리순)으로 매장 검색 가능

### 예약 시스템
- 사용자는 매장의 예약 가능 시간을 확인 후 예약 가능
- 파트너는 예약 요청을 승인 또는 거절 가능
- 예약 상태 관리 (대기중, 승인됨, 거절됨, 도착함, 완료됨, 취소됨, 노쇼)

### 키오스크 연동
- 매장 방문 시 키오스크를 통한 도착 확인
- 예약 시간 10분 전부터 도착 확인 가능

### 리뷰 시스템
- 예약 이용 후 리뷰 작성 가능
- 리뷰 작성자만 수정 가능, 작성자와 매장 관리자만 삭제 가능

### 알림 시스템
- 예약 상태 변경 시 사용자에게 알림 전송
- 알림 목록 조회 및 읽음 처리 기능

### 통계 기능
- 파트너를 위한 매장별 예약 통계 제공
- 기간별, 시간대별, 상태별 통계 분석

## 기여 방법
1. 프로젝트 포크
2. 기능 브랜치 생성 (`git checkout -b feature/amazing-feature`)
3. 변경 사항 커밋 (`git commit -m 'feat: 새로운 기능 추가'`)
4. 브랜치 푸시 (`git push origin feature/amazing-feature`)
5. Pull Request 생성

## 라이센스
이 프로젝트는 [MIT 라이센스](https://github.com/FaithCoderLab/table-booking-service/LICENSE.txt)를 따릅니다.
