# Spring boot 4 what changed

Spring Boot 4.x의 주요 변경사항을 실습하고 학습하기 위한 게시판 프로젝트입니다.

## 프로젝트 개요

### 목적
- Spring Boot 4.x의 새로운 기능 학습
- 3.x에서 4.x로의 변경사항 파악

### 작업 기간
2025-12-07 ~ 2025-12-08

### 주요 내용
- 기본 게시판 기능 구현
- Spring Boot 4.x 변경점 확인 및 적용
- Jakarta EE 11 기반 코드 작성

## 기술 스택

### Backend
- Spring Boot 4.0.x
- Spring Framework 7.0
- Spring Data JPA 2025.1
- Hibernate 6.4+
- Java 21

### 필수 요구사항
- JDK 17 이상
- Gradle 8.14 이상

### Database
- H2 Database

### Build Tool
- Gradle 8.14+

## 프로젝트 구조

```
spring-boot-4-what-changed/
├── demo/                          # 메인 프로젝트
│   └── src/main/java/
│       └── com/example/aboard/
│           ├── controller/        # REST API 컨트롤러
│           ├── dto/              # 데이터 전송 객체
│           ├── entity/           # JPA 엔티티
│           ├── repository/       # 데이터 접근 계층
│           └── service/          # 비즈니스 로직
```

## 구현 기능

### 게시판 기본 기능
- 게시글 작성, 조회, 수정, 삭제
- 게시글 목록 및 페이징
- 조회수 관리
- 비밀번호 기반 인증

### 댓글 시스템
- 댓글 작성 및 삭제
- 대댓글 (계층형 댓글) 지원
- IP 기반 사용자 식별
- IP 마스킹 및 색상 코드 생성

### 보안
- 비밀번호 암호화 (BCrypt)
- IP 주소 마스킹
- XSS 방지
