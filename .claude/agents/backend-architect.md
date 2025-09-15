# 📄 PRD (백엔드 기능 요구사항)

## 1. 개요
본 문서는 **카카오톡 유사 채팅 서비스**의 백엔드 기능 요구사항을 정의한다.  
백엔드는 Spring Boot를 기반으로 하며, **회원 인증, 메시지 관리, 그룹 채팅, 미디어 업로드, 알림 처리**를 담당한다.

---

## 2. 주요 기능

### 2.1 회원 관리
- **회원가입/로그인**
    - 전화번호 또는 이메일 기반 가입/로그인 지원
    - JWT 기반 인증 (Access Token + Refresh Token)
    - 소셜 로그인 확장 고려 (Google, Kakao 등)

- **프로필 관리**
    - 닉네임, 상태 메시지, 프로필 이미지 저장
    - 친구 목록 관리 (추가/차단)

---

### 2.2 채팅 관리
- **1:1 채팅**
    - 텍스트 메시지 송수신
    - 이미지/동영상/파일 전송
    - 메시지 상태: 전송됨/읽음 표시 (read receipt)

- **그룹 채팅**
    - 방 생성/초대/퇴장
    - 메시지 멘션 기능 (@user)
    - 공지사항 등록 기능
    - 관리자 권한 (강퇴, 알림설정)

- **메시지 저장/조회**
    - 메시지 영구 저장 (RDB + Blob/File Storage)
    - 최근 메시지 불러오기 (페이징)
    - 채팅방별 미디어 히스토리 조회

---

### 2.3 멀티미디어 처리
- **파일 업로드/다운로드**
    - 이미지/동영상/파일 업로드 (S3 또는 로컬 Storage)
    - CDN 경유 다운로드 지원
    - 업로드 시 바이러스 스캔/파일 크기 제한

- **썸네일/미리보기 생성**
    - 이미지 썸네일 자동 생성
    - 동영상 미리보기(첫 프레임 캡처)

---

### 2.4 알림 기능
- **푸시 알림**
    - FCM/APNs 기반 알림 전송
    - 새 메시지/멘션/공지 발생 시 알림

- **실시간 통신**
    - WebSocket or STOMP 기반 실시간 채팅
    - Redis Pub/Sub 기반 채팅 서버 스케일아웃 지원

---

### 2.5 관리/보안
- **로그/모니터링**
    - 사용자 액션 로그 (로그인, 채팅방 생성, 메시지 전송 등)
    - Prometheus/Grafana 모니터링

- **보안**
    - 비밀번호 암호화 (BCrypt)
    - JWT 토큰 서명/검증
    - 민감 데이터 마스킹 (로그/DB)
    - 메시지 전송 시 E2EE(종단간 암호화) 고려

---

## 3. 데이터베이스 설계 개요
- **Users**
    - id (ULID/UUID), email/phone, nickname, profileImage, createdAt
- **Friends**
    - userId, friendId, status(blocked, active)
- **ChatRoom**
    - roomId, type (direct/group), createdAt
- **RoomMembers**
    - roomId, userId, role (admin/member)
- **Messages**
    - messageId, roomId, senderId, type(text/image/file), content, readCount, createdAt
- **MediaFiles**
    - fileId, url, type, size, thumbnailUrl, createdAt

---

## 4. 비기능 요구사항
- **성능**
    - 메시지 전송/수신 지연 200ms 이하
    - 동시 접속자 1만 명 이상 처리 가능

- **확장성**
    - 멀티 인스턴스 배포 (Docker/Kubernetes)
    - Redis Pub/Sub을 통한 채팅 이벤트 브로드캐스팅

- **신뢰성**
    - DB Replication & Failover 구성
    - 메시지 전송 실패 시 재전송 로직


# 📄 기술문서 (백엔드 API 명세)

## 1. 인증/회원 관리 API

### 1.1 회원가입
- **POST** `/api/auth/signup`
- **Request**
```json
{
  "email": "test@example.com",
  "password": "1234",
  "nickname": "홍길동",
  "phone": "01012345678"
}
```
- **Response**
```json
{
  "userId": "01HZX1X4YW6QW8M4N3N3JYQ0R2",
  "email": "test@example.com",
  "nickname": "홍길동",
  "createdAt": "2025-09-13T12:00:00"
}
```

### 1.2 로그인
- **POST** `/api/auth/login`
- **Request**
```json
{
  "email": "test@example.com",
  "password": "1234"
}
```
- **Response**
```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token"
}
```

### 1.3 프로필 조회
- **GET** `/api/users/{userId}`
- **Response**
```json
{
  "userId": "01HZX1X4YW6QW8M4N3N3JYQ0R2",
  "nickname": "홍길동",
  "profileImage": "https://cdn.example.com/profiles/123.png",
  "statusMessage": "오늘도 화이팅!"
}
```

---

## 2. 채팅방 관리 API

### 2.1 채팅방 생성
- **POST** `/api/chat/rooms`
- **Request**
```json
{
  "type": "group",
  "name": "개발자 스터디방",
  "members": ["userA", "userB", "userC"]
}
```
- **Response**
```json
{
  "roomId": "01HZX3QWZP3E8M1YZ6J3DJF3YQ",
  "type": "group",
  "name": "개발자 스터디방",
  "createdAt": "2025-09-13T13:00:00"
}
```

### 2.2 채팅방 목록 조회
- **GET** `/api/chat/rooms?userId={userId}`
- **Response**
```json
[
  {
    "roomId": "01HZX3QWZP3E8M1YZ6J3DJF3YQ",
    "name": "개발자 스터디방",
    "lastMessage": "내일 모임 몇 시에?",
    "unreadCount": 3
  }
]
```

---

## 3. 메시지 API

### 3.1 메시지 전송
- **POST** `/api/chat/rooms/{roomId}/messages`
- **Request**
```json
{
  "senderId": "userA",
  "type": "text",
  "content": "안녕하세요!"
}
```
- **Response**
```json
{
  "messageId": "01HZX3V9CP2N7D1XK3W1PZJ9YT",
  "roomId": "01HZX3QWZP3E8M1YZ6J3DJF3YQ",
  "senderId": "userA",
  "type": "text",
  "content": "안녕하세요!",
  "createdAt": "2025-09-13T13:30:00"
}
```

### 3.2 메시지 조회
- **GET** `/api/chat/rooms/{roomId}/messages?lastMessageId={id}&limit=20`
- **Response**
```json
[
  {
    "messageId": "01HZX3V9CP2N7D1XK3W1PZJ9YT",
    "senderId": "userA",
    "type": "text",
    "content": "안녕하세요!",
    "readCount": 2,
    "createdAt": "2025-09-13T13:30:00"
  }
]
```

---

## 4. 파일 업로드 API

### 4.1 파일 업로드
- **POST** `/api/files/upload`
- **Form-Data**
    - file: (binary)
- **Response**
```json
{
  "fileId": "01HZX4K7VN9EZT1C2P3Y9X6W1",
  "url": "https://cdn.example.com/files/abc123.jpg",
  "thumbnailUrl": "https://cdn.example.com/files/abc123_thumb.jpg",
  "size": 204800
}
```

---

## 5. 알림/실시간 API

### 5.1 WebSocket 연결
- **Endpoint**: `ws://server.com/ws/chat`
- **Protocol**: STOMP
- **구독 예시**
    - `/topic/room/{roomId}` (방 메시지 수신)
- **전송 예시**
    - `/app/chat/{roomId}` (방에 메시지 전송)

---

## 6. 관리/보안

### 6.1 관리자 API
- **POST** `/api/chat/rooms/{roomId}/kick`
- **Request**
```json
{
  "adminId": "userAdmin",
  "targetId": "userB"
}
```
- **Response**
```json
{
  "success": true,
  "message": "userB removed from room"
}
```

---

## 7. 비고
- 모든 API는 **JWT 인증** 필요 (`Authorization: Bearer <token>`)
- 시간은 UTC 기준 ISO-8601 포맷 사용
- 오류 응답은 표준화된 에러 코드와 메시지를 반환


# 🤖 Claude 사용 가이드

## 1. 개요
이 문서는 본 프로젝트에서 **Claude + MCP**를 어떻게 활용할 것인지 정의한다.  
Claude는 단순 대화형 AI를 넘어, **GitHub 자동화 / 코드 리뷰 / 문서 생성 / DB 질의** 등 개발 워크플로우를 지원한다.

---

## 2. Claude API 연동
- 사용 모델: `claude-4-sonnet`
- 호출 방식: Spring Boot WebClient → Anthropic API
- API Key 관리: 환경 변수 `ANTHROPIC_API_KEY` 사용
- 보안: `.env`, CI/CD Secret Manager 활용

---

## 3. MCP 활용 모듈
현재 프로젝트에서 활성화할 MCP 모듈:

- **mcp-git**
    - 로컬 Git 레포지토리 조작 (commit, push, branch 생성 등)
- **mcp-github**
    - GitHub Issue/PR 자동 생성
    - 커밋 메시지 요약 및 리뷰 코멘트 자동화
- **mcp-fs**
    - `docs/` 폴더 내 문서 읽기/수정 자동화
- **mcp-db** (확장 예정)
    - MySQL/Postgres 질의 실행 및 결과 요약

---

## 4. 주요 사용 시나리오
- **PR 자동화**
    - 자연어 입력: “로그인 에러 수정한 코드로 PR 올려줘”
    - Claude → Git commit → push → PR 생성 + 설명문 작성
- **Issue 생성**
    - 버그 리포트 텍스트 전달 → Claude가 GitHub Issue 생성
- **문서화**
    - `todoList.md` 기반 → 진행 상황 요약 자동 작성
    - Swagger API 문서 → 기술문서 자동 업데이트
- **코드 리뷰**
    - Claude가 최근 커밋/PR 리뷰 요약 제공
    - 보안/성능 관련 개선 포인트 제안

---

## 5. 보안 가이드
- GitHub 토큰은 절대 코드/명령어에 직접 노출하지 않는다
- 모든 API Key/Token은 환경 변수 또는 Secret Manager로 관리
- Claude에게 실행 권한을 줄 때는 **필요 최소 권한만 부여**

---

## 6. 향후 확장
- CI/CD 파이프라인에 Claude 리뷰 자동 삽입
- 에러 로그 자동 요약 및 Slack 알림 연동
- 서비스 운영 데이터 분석 (DB MCP 활용)


# 🌿 Git Flow Branch & Commit Convention

## 1. Branch 전략

Git Flow 기본 브랜치 구조는 다음과 같습니다:

- **main**
    - 실제 서비스 운영(Production)에 배포되는 안정화된 브랜치
- **develop**
    - 개발 진행의 기준이 되는 브랜치 (다음 릴리즈 후보)

### 📌 보조 브랜치 유형
| 브랜치 종류 | 네이밍 규칙 | 용도 |
|-------------|-------------|------|
| **feature/** | `feature/<이슈번호-설명>` | 새로운 기능 개발 |
| **bugfix/** | `bugfix/<이슈번호-설명>` | 개발 중 발견된 버그 수정 |
| **hotfix/** | `hotfix/<이슈번호-설명>` | 운영 중 긴급 버그 수정 |
| **release/** | `release/<버전>` | 배포 전 QA 및 안정화 |
| **chore/** | `chore/<설명>` | 빌드, 설정, 문서 등 유지보수 |

### 📌 브랜치 네이밍 예시

feature/123-user-login
bugfix/456-api-error
hotfix/789-prod-crash
release/1.0.0
chore/update-ci-config

---

## 2. Commit 메시지 컨벤션

### 📌 기본 구조
<타입>(<범위>): <간단 설명>

본문 (선택)
Footer (선택, issue tracker ID 등)

### 📌 타입 (Conventional Commits 기반)
| 타입 | 설명 |
|------|------|
| **feat** | 새로운 기능 추가 |
| **fix** | 버그 수정 |
| **docs** | 문서 추가/수정 |
| **style** | 코드 포맷팅, 세미콜론 누락 등 (비즈니스 로직 변경 없음) |
| **refactor** | 코드 리팩토링 |
| **test** | 테스트 코드 추가/수정 |
| **chore** | 빌드/도구/환경설정 관련 작업 |
| **perf** | 성능 개선 |
| **ci** | CI/CD 설정 변경 |
| **revert** | 이전 커밋 되돌리기 |

### 📌 커밋 메시지 예시
feat(auth): 로그인 API 구현

JWT 기반 로그인 API 추가

refresh token 로직 구현

Swagger 문서 업데이트

Resolves: #123


fix(user): 잘못된 비밀번호 검증 로직 수정

bcrypt 비교 로직에서 salt 처리 누락 수정

chore: ESLint, Prettier 설정 추가

---

## 3. Pull Request 규칙

- **PR 제목**: `[타입] 작업 내용 요약`  
  예: `[feat] 사용자 로그인 기능 추가`
- **PR 단위**: 가능한 한 작은 기능 단위로 (하나의 PR = 하나의 명확한 목적)
- **리뷰어 최소 1명 승인** 후 머지

---

## 4. Merge 전략

- `main` ← `release/*` : **Fast-forward 금지**, merge commit 유지
- `develop` ← `feature/*` : squash 또는 merge commit (팀 합의에 따라 선택)
- `hotfix/*` : `main`과 `develop`에 동시에 반영

---

## ✅ 요약

- Branch 네이밍: `feature/`, `bugfix/`, `release/`, `hotfix/`, `chore/`
- Commit 메시지: **Conventional Commits** 기반 (`feat`, `fix`, `docs` 등)
- PR 규칙: 작은 단위, `[타입] 작업 내용 요약`
- Merge 전략: Git Flow에 맞게 main/develop 기준 관리
