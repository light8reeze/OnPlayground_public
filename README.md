# 웹앱퀴즈 플랫폼 (WebAppQuiz Platform)

실시간 웹 기반 퀴즈 게임 플랫폼입니다. 관리자가 퀴즈를 관리하고 사용자들이 실시간으로 참여할 수 있는 시스템을 제공합니다.

## 📋 프로젝트 구조

이 프로젝트는 두 개의 주요 컴포넌트로 구성되어 있습니다:

### 1. WebAppQuiz (메인 서버)
- **위치**: `webappquiz/`
- **기술 스택**: Java 21, Spring Boot 3.4.2, MongoDB, Redis, WebFlux
- **기능**:
  - 실시간 퀴즈 게임 서버
  - WebSocket 기반 실시간 통신
  - 게임 채널 관리
  - 점수판 및 랭킹 시스템
  - Protocol Buffers를 통한 데이터 통신

## 🚀 시작하기

### 사전 요구사항

- Java 21 이상
- PHP 7.4 이상
- MongoDB
- Redis
- Composer (PHP 의존성 관리)
- Gradle (Java 빌드 도구)

### 설치 및 실행

#### 1. WebAppQuiz 서버 설정
```bash
cd webappquiz
./gradlew bootRun
```

또는 JAR 파일로 빌드:
```bash
./gradlew build
java -jar build/libs/webappquiz-0.0.1-SNAPSHOT.jar
```

## 📁 주요 디렉토리 구조

```
server-main/
└── webappquiz/                 # 메인 게임 서버
    ├── src/main/java/com/webappquiz/webappquiz/
    │   ├── component/          # 게임 컴포넌트
    │   ├── config/             # 설정
    │   ├── Data/               # 데이터 모델
    │   └── ...
    ├── proto/                  # Protocol Buffers 정의
    ├── build.gradle            # 빌드 설정
    └── settings.gradle
```

## 🔧 주요 기능

### 관리자 기능 (DataControl)
- 🔐 코드 기반 인증 시스템
- 📊 퀴즈 데이터 관리
- 📄 CSV 파일을 통한 대량 퀴즈 업로드
- 🎯 OX 퀴즈 관리
- 👥 사용자 권한 관리

### 게임 서버 기능 (WebAppQuiz)
- 🎮 실시간 멀티플레이어 퀴즈 게임
- 📡 WebSocket 기반 실시간 통신
- 🏆 실시간 점수판 및 랭킹
- 🔄 게임 채널 관리
- 💾 MongoDB를 통한 데이터 영속화
- ⚡ Redis를 통한 실시간 데이터 동기화

## 🛠 기술 스택

### 백엔드
- **Java**: Spring Boot 3.4.2, WebFlux (Netty 기반)
- **데이터베이스**: MongoDB, Redis
- **통신**: Protocol Buffers, WebSocket

### 프론트엔드
- **CSS 프레임워크**: Tailwind CSS
- **JavaScript 라이브러리**: jQuery
- **실시간 통신**: WebSocket

### 빌드 도구
- **Java**: Gradle
- **PHP**: Composer
- **Protocol Buffers**: protobufjs-cli

## 📝 개발 참고사항

- Java 21의 최신 기능을 활용
- Spring Boot 3.x의 비동기 처리 및 WebFlux 사용
- Protocol Buffers를 통한 효율적인 데이터 직렬화
- Redis를 통한 실시간 데이터 캐싱 및 동기화
- MongoDB를 통한 NoSQL 데이터 저장

---

이 프로젝트는 교육용 퀴즈 플랫폼으로 개발되었으며, 실시간 상호작용과 확장성을 고려하여 설계되었습니다.
