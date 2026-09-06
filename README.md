# shopping_mall

주문 API 를 직접 만들어보면서 백엔드 기술을 학습하는 프로젝트입니다.

## 목적

기능을 빠르게 완성하는 것보다, **왜 그렇게 구현했는지 설명할 수 있는 것**을 목표로 합니다.
그래서 이 저장소에는 코드뿐 아니라 학습 기록(`docs/journal`)과
학습 방식을 정의한 가이드(`CLAUDE.md`)가 함께 들어 있습니다.

작은 기능부터 시작해서, 실제로 문제가 발생한 뒤에 해결하는 순서로 진행합니다.

```
기능 구현 → 트랜잭션 → 동시성 → 성능 측정 → 대규모 트래픽
```

문제가 생기지 않은 상태에서 미리 기술을 도입하지 않는 것을 원칙으로 합니다.

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 4.2.0-SNAPSHOT |
| 데이터 접근 | Spring Data JPA (Hibernate) |
| 웹 | Spring Web MVC, Bean Validation |
| DB | H2 (기본), MariaDB (성능 측정용) |
| 빌드 | Maven (`mvnw`) |
| 기타 | Lombok, DevTools |

> Spring Boot 버전은 정식 릴리스가 아닌 **개발 스냅샷**입니다.
> 최신 기능을 확인하려는 목적이며, 시점에 따라 동작이 달라질 수 있습니다.

## 실행 방법

> 설정 파일(`application.properties`)이 프로젝트 루트에 있으므로
> **반드시 프로젝트 루트에서 실행**해야 합니다.

### 기본 실행 (H2 인메모리)

```bash
./mvnw spring-boot:run
```

| 항목 | 값 |
|---|---|
| 포트 | 9091 |
| DB | `jdbc:h2:mem:testdb` |
| H2 콘솔 | http://localhost:9091/h2-console |

애플리케이션이 뜰 때마다 테이블이 새로 생성되고(`ddl-auto=create`), 종료하면 데이터가 사라집니다.

### 테스트

```bash
./mvnw test
```

SQL, 트랜잭션 경계, 커넥션 풀 상태가 로그로 출력됩니다.

### MySQL(MariaDB) 로 실행

성능 측정처럼 실제 네트워크 왕복이 필요한 경우에 사용합니다.

1. 스키마 생성

```sql
CREATE DATABASE shopping_mall_test DEFAULT CHARACTER SET utf8mb4;
```

2. 프로젝트 루트에 `application-mysql.properties` 생성
   (접속 정보가 들어가므로 저장소에 포함하지 않습니다)

```properties
spring.datasource.url=jdbc:mariadb://127.0.0.1:3306/shopping_mall_test
spring.datasource.username=<사용자명>
spring.datasource.password=<비밀번호>
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=create
```

3. 프로파일 지정 후 실행

```bash
./mvnw test -Dspring.profiles.active=mysql
```

## 프로젝트 구조

```
com.truthgarnet.shopping
├── order        주문      Controller, Service, Entity, Repository, DTO
├── orderItem    주문 항목  Entity, Repository, DTO
└── product      상품      Entity, Repository
```

```
docs/journal    날짜별 학습 기록
CLAUDE.md       학습 방식 가이드
```

## 학습 기록

날짜별 기록: [docs/journal](docs/journal)

| 날짜 | 주제 |
|---|---|
| [2026-08-28](docs/journal/2026-08-28.md) | API 메서드 네이밍, 단건 조회 vs `IN` 절 조회 |
| [2026-09-02](docs/journal/2026-09-02.md) | `@OneToMany` vs FK 필드, `GenerationType` 전략 |
| [2026-09-03](docs/journal/2026-09-03.md) | `@Transactional` 적용 범위, 롤백 정책, 프록시와 CGLIB |
| [2026-09-04](docs/journal/2026-09-04.md) | `@Transactional` import, `@NoArgsConstructor`, `record` |

## 진행 상황

- [x] 주문 생성 API (재고 차감, DTO 응답)
- [x] `@Transactional` 적용 및 롤백 검증 테스트
- [x] 반복 조회 vs `IN` 절 일괄 조회 성능 비교 (H2 / MariaDB)
- [ ] 동시성 제어 — 재고 차감 시 lost update 재현 후 락 적용
- [ ] 부하 테스트 — RPS 를 올려가며 병목 지점 확인
