# 1. Spring Boot 4.x vs 3.x 주요 변경사항

## 릴리즈 정보
- **Spring Boot 4.0.0**: 2025년 11월 20일 공식 출시
- **기반 프레임워크**: Spring Framework 7.0

## 프레임워크 업데이트

| 프레임워크 | Spring Boot 3.x | Spring Boot 4.0 |
|----------|----------------|----------------|
| Spring Framework | 6.x | 7.0 |
| Spring Security | 6.x | 7.0 |
| Spring Data | 2024.x | 2025.1 |
| Micrometer | 1.13.x | 1.16 |
| Hibernate | 6.2.x | 6.4+ |
| Gradle | 7.5+ / 8.x | 8.14+ / 9.x |

##  버전별 지원 기간

| 버전 | 출시일 | OSS 지원 종료 | 상용 지원 종료 |
|------|--------|-------------|--------------|
| 2.7.x | 2022-05 | 2024-11 | 2025-11 |
| 3.0.x | 2022-11 | 2024-05 | 2025-05 |
| 3.1.x | 2023-05 | 2024-11 | 2025-11 |
| 3.2.x | 2023-11 | 2025-05 | 2026-05 |
| 3.3.x | 2024-05 | 2025-11 | 2026-11 |
| 4.0.x | 2025-11 | 2027-05 | 2028-05 |

## 주요 새 기능

### 1) HTTP Service Clients (HTTP 서비스 클라이언트)
인터페이스만 정의하면 Spring이 자동으로 구현체를 생성해주는 기능

**사용 예시**:
```java
public interface UserClient {

    @GetExchange("/users/{id}")
    User getUser(@PathVariable Long id);

    @PostExchange("/users")
    User createUser(@RequestBody User user);
}
```

**이전 방식 (Spring Boot 3.x)**:
```java
// RestTemplate이나 WebClient를 직접 사용
@Service
public class UserService {
    private final RestTemplate restTemplate;

    public User getUser(Long id) {
        return restTemplate.getForObject("/users/" + id, User.class);
    }
}
```

**장점**:
- 코드 간소화
- 타입 안정성 향상
- Feign Client와 유사하지만 Spring 공식 지원

### 2) API 버저닝 (API Versioning)
REST API 버전 관리를 위한 내장 지원

**설정 방법**:
```properties
# application.properties
spring.mvc.apiversion.enabled=true
spring.mvc.apiversion.header-name=API-Version
spring.mvc.apiversion.default-version=1.0
```

**사용 예시**:
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    @ApiVersion("1.0")
    public List<UserV1> getUsersV1() {
        // 버전 1.0 응답
    }

    @GetMapping
    @ApiVersion("2.0")
    public List<UserV2> getUsersV2() {
        // 버전 2.0 응답 (새 필드 추가)
    }
}
```

**이전 방식 (Spring Boot 3.x)**:
```java
// URL 경로에 버전을 직접 포함하거나 커스텀 구현 필요
@GetMapping("/api/v1/users")
@GetMapping("/api/v2/users")
```

### 3) OpenTelemetry 통합
관측성(Observability) 향상을 위한 OpenTelemetry 지원

**의존성 추가**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-opentelemetry</artifactId>
</dependency>
```

**자동 구성**:
- 메트릭 및 트레이스 자동 수집
- OTLP를 통한 데이터 내보내기
- 별도 설정 없이 즉시 사용 가능

**이전 방식 (Spring Boot 3.x)**:
```java
// Micrometer를 수동으로 설정하거나 서드파티 라이브러리 사용
```

### 4) Kotlin Serialization 지원
Kotlin 프로젝트를 위한 네이티브 직렬화 지원

**의존성**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-kotlinx-serialization-json</artifactId>
</dependency>
```

**사용 예시**:
```kotlin
@Serializable
data class User(
    val id: Long,
    val name: String,
    val email: String
)
```

### 5) RestTestClient
통합 테스트 개선

**사용 예시**:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void testGetUser() {
        User user = restTestClient.get()
            .uri("/api/users/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(User.class)
            .returnResult()
            .getResponseBody();
    }
}
```


## Breaking Changes (주요 변경사항)

### 1) 모듈화 (Modularization)
- Spring Boot 코드베이스가 완전히 모듈화됨
- 더 작고 집중된 JAR 파일 제공
- 필요한 모듈만 선택적으로 포함 가능

**영향**:
```xml
<!-- 이전: 하나의 큰 starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 4.0: 필요에 따라 세분화된 모듈 선택 가능 -->
```

### 2) Jackson 2 지원 Deprecated
- Jackson 2는 deprecated 상태로 제공
- Jackson 3로 마이그레이션 권장

### 3) MongoDB Health Indicator 패키지 변경
```java
// 이전 (3.x)
import org.springframework.boot.actuate.data.mongo.MongoHealthIndicator;

// 변경 (4.0)
import org.springframework.boot.actuate.mongo.MongoHealthIndicator;
```

### 4) Auto-configuration 클래스 변경
- Public 멤버 제거 (상수 제외)
- 직접 접근하던 코드는 수정 필요

## Java 버전 지원

| Spring Boot 버전 | 최소 Java 버전 | 최대 지원 Java 버전 |
|-----------------|---------------|-------------------|
| 3.x | Java 17 | Java 21 |
| 4.0 | Java 17 | Java 25 |

**중요**: Spring Boot 4.0은 Java 17 호환성을 유지하면서 Java 25까지 지원

## 성능 개선
- 시작 시간 개선
- 메모리 사용량 최적화
- 모듈화로 인한 애플리케이션 크기 감소

---

**OSS 지원**: 무료 버그 수정 및 보안 패치
**상용 지원**: 유료 확장 지원 (Spring Boot Enterprise)

---

# 참고 자료

## 공식 문서
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Spring Boot 4.0.0 Available Now](https://spring.io/blog/2025/11/20/spring-boot-4-0-0-available-now/)
- [Spring Framework 7.0 Documentation](https://docs.spring.io/spring-framework/reference/)

## 학습 자료
- [Spring Boot 4 Key Features](https://loiane.com/2025/08/spring-boot-4-spring-framework-7-key-features/)
- [OpenRewrite](https://docs.openrewrite.org/recipes/java/spring/boot3/upgradespringboot_3_0)
- [Spring Boot Migrator](https://github.com/spring-projects-experimental/spring-boot-migrator)

---

# 요약 비교표

| 특징 | Spring Boot 3.x | Spring Boot 4.x |
|-----|----------------|----------------|
| **Java 버전** | 17, 21 | 17, 21, 25 |
| **패키지** | jakarta.* | jakarta.* |
| **Spring Framework** | 6.0 | 7.0 |
| **Servlet API** | 6.0 | 6.0+ |
| **JPA** | 3.1 | 3.1+ |
| **HTTP Client** | Apache 5.x | Apache 5.x |
| **Native Image** | 개선됨 | 완전 지원 |
| **HTTP Service Client** | ❌ | ✅ |
| **API Versioning** | ❌ | ✅ |
| **OpenTelemetry** | 일부 지원 | 완전 자동화 |
| **모듈화** | ❌ | ✅ |
| **지원 상태** | 활발 | 최신 |

