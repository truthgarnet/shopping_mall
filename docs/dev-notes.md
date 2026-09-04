# Dev Notes

## 2026-08-28 주문 API

### API 메서드 네이밍

- `고민`: `insertOrders()` vs `createOrder()`
- `결정`: `createOrder()`
- `이유`: insertOrders()는 서비스에서 사용하기에는 DB에 사용하는 단어고, OrderItem 생성까지 하는 서비스 단위에서는 createOrder()을 사용하는 게 맞다고 생각

### List로 조회할 때, DB에서는 단일 건으로 받을 것인지, 리스트 자체로 받을 것인지

* `고민`: repository.findBySeq(Long seq) vs repository.findBySeqIn(List`<Long>`seq)
* `결정`: findBySeqIn(List`<Long>`seq)
* `이유`: DB 왕복비용에 생각보다 많은 비용이 들어감

```java
@Test
void findByProductSeqIn() {
List<Long> seqs = new ArrayList<>();

IN 조회: 300.117ms
```

```java
@Test
void findByProductSeq() {

    long start = System.nanoTime();

    for (long i = 1; i <= 1000; i++) {
        productRepository.findByProductSeq(i);
    }

    long end = System.nanoTime();

    System.out.println("개별 조회: " + (end - start) / 1_000_000.0 + "ms");
}
개별 조회: 444.1992ms
```

## 2026-09-02

### @OneToMany vs FK 값만 필드로

#### @OneToMany

**장점**

* order.getItems() 로 객체로 그대로 탐색 가능 -> 따로 쿼리문이 필요하지 않음
* cascade = CascadeType.ALL, orphaRemoval = true 로 Order 저장 / 삭제 시 OrderItem도 같이 저장 / 삭제되도록 할 수 있음

**단점**

* N+1 문제 발생: 사용하기에는 편리하지만, 항상 대두되는 게 N+1문제가 발생한다. 그래서 이 문제를 해결하는 것보다, 필드로 선언하면 발생하지 않는 문제기 때문에 쿼리로 가는 게 편리하다는 의견도 존재했다.
  
  * 주문 목록을 조회하고 각 주문의 items를 순회하면 주문 개수만큼 추가 쿼리가 나감 (fetch join 이나 @EntityGraph로 따로 신경 써야 함)
* List 컬렉션에 orphamRemoval / cascade REMOVE 쓰면 Hiberate가 "전부 삭제 후 재삽입" 방식으로 동작해서 예상 못한 쿼리가 많이 나갈 수 있음
* Lazy 로딩이면 트랜잭션 / 세션 밖에서 접근 시 LazyInitializationException, Eager면 항상 다 긁어옴
* 엔티티를 그대로 JSON 응답에 쓰면 순환 참조 위험 가능성

#### FK 값만 필드로

**장점**

* 직접 쿼리를 하기 때문에, 명시적이고 숨겨진 쿼리가 나가지 않음
* 엔티티 간 결합도가 낮아져, 모듈 분리나 클린 아키텍처 지향일 때 유리함
* 저장/삭제를 서비스 레이어를 명시적으로 컨트롤 하기때문에, 진행을 코드만 보고 알 수 있음

**단점**

* cascade 자동화가 없어서, Order 생성 시 Item들 저장, Order 삭제 시 Item들 삭제를 서비스 코드에서 직접 해야함
* 연관 데이터 조회/삭제를 위한 리포지토리 메서드를 매번 직접 작성
* JPA가 참조 무결성을 안 챙겨줘서, DB 차원에서 FK 제약조건을 따로 걸어야함

---

### GenerationType.IDENTITY vs GenertationType.SEQUENCE vs GenerationType.AUTO

#### IDENTITY

- **DB에서 위임하는 전략** (MySQL AUTO_INCREMENT, H2/PostgreSQL IDENTITY 컬럼 등)
- Hibernate 입장에서 영속성 컨텍스트에 엔티티를 관리하려면 ID가 필요한데, 그 ID를 얻으려면 무조건 즉시 INSERT 날려야함
- JDBC batch insert가 사실상 안됨. saveAll()를 호출해도 하나마다 **개별 INSERT가 즉시 나감**

#### SEQUENCE

- INSERT 전에 미리 시퀀스에서 다음 값을 받아둘 수 있어서, Hibernate가 여러 INSERT를 진짜 batch를 묶어서 보낼 수 있음
- 

#### AUTO

- JPA Hibernate가 알아서 골라서 개발자가 DB 종류를 신경 안 써도 되고, DB를 바꿔도 코드는 그대로 사용가능
- H2처럼 시퀀스를 지원하는 DB에서는 보통 SEQUNECE 전략 사용
  
  - IDENTITY와 SEQUENCE 둘다 지원하지만, SEQUNECE를 선택하는 이유
  - 우선순위에 따라 선택하게 됨(Hibernate 버전마다 다르게 진행됨으로 확인이 필요하다)
  
  | Hibernate   | `AUTO`의기본적인해석             | MySQL에서    | PostgreSQL/H2등Sequence지원DB |
| ----------- | -------------------------------- | ------------ | ----------------------------- |
| **4.x이하** | DBDialect의`native` 전략         | **IDENTITY** | 주로SEQUENCE                  |
| **5.x**     | `SequenceStyleGenerator`         | **TABLE**    | SEQUENCE                      |
| **6.x**     | `SequenceStyleGenerator`         | **TABLE**    | SEQUENCE                      |
| **7.x**     | DB기능에따라SEQUENCE/TABLE등선택 | **TABLE**    | SEQUENCE                      |
  
  - 이는 앞서 말한 **batch insert (성능)** 가 가능하기 때문에, Hibernate는 AUTO로 설정 했을 때 먼저 SEQUENCE를 선택하게 된다.

#### TABLE

- SEQUENCE를 제공하지 않는 DB를 대체하기 위해서 사용하는 방법으로, PK 번호를 발급하기 위한 별도의 테이블을 하나 두는 방식이다.
- `orderRepository.save();` 을 작업하면 아래와 같이 PK를 검색하는 쿼리가 발생한다.

```sql
SELECT next_val
FROM id_generator
WHERE generator = 'order'
FOR UPDATE;
```

---

## 260903

### @Autowired

- 이미 등록된 Bean에게 연결하는 것
- ex) 사장이나 비서에게 연결해주는 것

### @Transacational은 어디에 적용시켜야 하는 가?

- 메서드 단계에서 적용해야한다.
- orderItemRepository.saveAll이 문제가 발생하면, 앞서 상품과 주문의 정보도 롤백되어야한다.
  - 그렇기 때문에, OrderService에 @Transacational를 붙이기로 결정

### 롤백 정책

- `Unchecked` vs `Checked`
  - Unchecked: RuntimeExection과 그 자손들, Error 가 터지면 자동으로 롤백
  - Checked: Exception을 상속하지만 RuntimeException은 아닌 것들
    - ex) IOExcetpion
- Checked 예외는 예상 가능한 비스니적으로 정상 처리 가능한 상황
  - ex) 재시도하거나 사용자에게 안내 가능
- UnkChecked는 버그 / 시스템 오류이기 때문에 한 작업을 다 무를 수 밖에 없는 상황

### 프록시 만드는 두 가지 방법

1. JDK 동적 프록시: 대상 클래스가 인터페이스를 구현하고 있으면, 그 인터페이스를 흉내 내는 프록시 생성
2. CGLIB: 인터페이스가 없다면, 그 클래스 자체를 상속하는 자식 클래스를 만들어서 메서드를 오버라이드

! 여기에서 프록시를 만들기 위해서는 `public`으로 구현해야하는 이유가 나온다.
인터페이스를 구현하기 위해서는 public으로 선언해야하는 데, 그렇다면 CGLIB는 public 이 아닌 `protected`와 `private`으로 구현할 수 있지 않을 까 싶은데, 이는 **Spring**에서 막아놨다.

=> 그 이유는 Spring에서는 코드로 봐서는 이것이 JDK 동적 프록시인지 CGLIB인지 모르기 때문이다. 그렇기 때문에, Spring은 JDK이든 `CGLIB`이든 무조건 public 만 지원한다는 규칙을 정했다.

### CGLIB 가 무엇인가?

런타임에 자바 바이트코드를 직접 생성해서 클래스를 만들어주는 라이브러리

OrderService 같은 클래스를 CGLIB한테 주면, CGLIB이 런타임에 그 클래스를 상송하는 새로운 자식 클래스를 하나 즉석에서 만들어줌

### Jackson은 필드가 아니라 getter를 본다

처음 알았는데, **Jackson**은 public 메서드 목록을 살펴보면서, 이름이 getXxx()나 isXxx() 패턴이면, 프로퍼티라고 인식한다.

```java
public String getProductName() {
  return productName;
}
```

이는 Jackson만의 규칙이 아닌 **JavaBean 스펙**이라는 초창기 부터 있던 관례이다.

