# 💬 Chatting Server

카카오톡 유사 실시간 채팅 서비스의 백엔드 서버입니다.

## 🎯 프로젝트 개요

Spring Boot 3.5.5 기반의 DDD 아키텍처를 적용한 채팅 서비스로, 실시간 메시징, 파일 공유, 푸시 알림 등의 기능을 제공합니다.

## 🚀 주요 기능

### 👥 사용자 관리
- JWT 기반 인증/인가
- 이메일/전화번호 기반 회원가입
- 프로필 관리 (닉네임, 상태메시지, 프로필 이미지)
- 친구 추가/차단 관리

### 💬 채팅 서비스
- 1:1 채팅 및 그룹 채팅
- 실시간 메시지 송수신 (WebSocket)
- 메시지 읽음/읽지않음 상태 관리
- 멘션 기능 (@user)
- 메시지 검색 기능

### 📁 파일 관리
- 이미지/동영상/문서 파일 업로드
- 썸네일 자동 생성
- 파일 다운로드 및 스트리밍
- 바이러스 스캔 및 보안 검증

### 🔔 알림 서비스
- FCM 기반 푸시 알림
- 실시간 알림 (새 메시지, 멘션)
- 알림 설정 관리

### 🛡️ 보안 및 관리
- 데이터 암호화 (AES-256)
- Rate Limiting
- 관리자 시스템
- 시스템 모니터링 (Prometheus/Grafana)

## 🏗️ 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Security 6**
- **Spring Data JPA**
- **WebSocket + STOMP**

### Database
- **MySQL/PostgreSQL** (Production)
- **H2** (Development)

### Infrastructure
- **Redis** (Session, Pub/Sub)
- **AWS S3** (File Storage)
- **FCM** (Push Notification)
- **Docker** (Containerization)

### Monitoring
- **Prometheus** (Metrics)
- **Grafana** (Visualization)

## 📁 프로젝트 구조 (DDD)

```
src/main/java/com/eomyoosang/chat/
├── domain/                    # 도메인 레이어
│   ├── user/                 # 사용자 도메인
│   ├── chat/                 # 채팅 도메인
│   ├── message/              # 메시지 도메인
│   ├── file/                 # 파일 도메인
│   └── shared/               # 공통 도메인
├── application/              # 애플리케이션 레이어
├── infrastructure/           # 인프라스트럭처 레이어
└── presentation/            # 프레젠테이션 레이어
```

## 🚀 시작하기

### 필수 요구사항
- Java 17+
- Docker & Docker Compose
- MySQL 8.0+ (선택사항)

### 설치 및 실행

1. **저장소 클론**
```bash
git clone https://github.com/EomYoosang/chatting-server.git
cd chatting-server
```

2. **애플리케이션 실행 (개발 모드)**
```bash
./gradlew bootRun
```

3. **Docker로 실행**
```bash
docker-compose up -d
```

### 환경 설정

`application.properties` 파일에서 다음 설정을 확인하세요:

```properties
# Database
spring.datasource.url=jdbc:h2:mem:chatdb
spring.datasource.username=sa
spring.datasource.password=

# JWT
jwt.secret=your-secret-key
jwt.expiration=900000

# File Upload
file.upload-dir=./uploads
file.max-size=10MB
```

## 📡 API 문서

### 인증 API
```http
POST /api/auth/signup      # 회원가입
POST /api/auth/login       # 로그인
POST /api/auth/refresh     # 토큰 갱신
```

### 채팅 API
```http
GET    /api/chat/rooms           # 채팅방 목록
POST   /api/chat/rooms           # 채팅방 생성
GET    /api/chat/rooms/{id}/messages  # 메시지 조회
POST   /api/chat/rooms/{id}/messages  # 메시지 전송
```

### WebSocket 연결
```
ws://localhost:8080/ws/chat
```

### 개발 도구
- **H2 Console**: http://localhost:8080/h2-console
- **API 문서**: http://localhost:8080/swagger-ui.html (예정)

## 🧪 테스트

```bash
# 단위 테스트
./gradlew test

# 통합 테스트
./gradlew integrationTest
```

## 📊 모니터링

- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus
- **Grafana Dashboard**: http://localhost:3000 (Docker 환경)

## 📝 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 👥 개발팀

- **엄유상** - *Backend Developer* - [@EomYoosang](https://github.com/EomYoosang)

## 🔗 관련 링크

- [프로젝트 이슈](https://github.com/EomYoosang/chatting-server/issues)
- [프로젝트 보드](https://github.com/EomYoosang/chatting-server/projects)
- [기술 문서](./docs/)

---

⭐ 이 프로젝트가 도움이 되었다면 Star를 눌러주세요!