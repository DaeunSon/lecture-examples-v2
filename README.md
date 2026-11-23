# 📝 과제 HW01 — 나머지 도메인 연관관계 끊기

| 항목 | 내용 |
| --- | --- |
| 마감 | **11/18 (수) 13:30** |
| 유형 | 복습 · 예상 2시간 |
| 시작 브랜치 | `homework/ddd-1` (이 브랜치) |
| 정답 예시 | `lectures/ddd/step2-done` |
| 이어지는 수업 | Day 1 · DDD ① 전략적 설계 (11/17) |

## 왜 하나요?

어제 수업에서 `상품 ↔ 주문` 경계를 **ID 간접 참조**로 끊었습니다. 같은 원칙으로 나머지 경계를 끊으면,
각 패키지가 다른 도메인을 객체로 붙잡지 않게 되고 — 나중에 서비스로 떼어낼 수 있는 상태가 됩니다.

## 할 일

**필수**

- [x] `Cart.user`, `CartItem.product` → `userId`, `productId` (Long)
- [x] `Seller.user`, `Seller.products` ↔ `Product.seller` 양방향 참조 제거 — 어느 쪽이 ID 를 들고 있어야 하는지 근거와 함께
- [x] `Order.user`, `Order.payment` → `userId`, `paymentId`
- [x] `OrderItem.product` → `productId` + **주문 당시 상품명·가격 스냅샷** (수업에서 함께 한 부분 — 본인 코드로 이어서 해도 됩니다)
- [x] `DataInitializer`, `OrderService` 를 바뀐 구조에 맞게 고쳐 `./gradlew bootRun` 후 주문이 성공

**도전**

- [x] 경계마다 "스냅샷이 필요한가?"를 표로 정리 (예: 주문 → 상품 가격은 필요 / 장바구니 → 상품 가격은 불필요, 왜?)

## 풀이 노트

### Seller ↔ Product — 누가 ID 를 들고 있나?

`Product.sellerId` 가 판매자 ID 를 들고, `Seller.products` 컬렉션은 제거했다.

- **의존 방향**: 상품은 판매자 없이 존재할 수 없지만, 판매자는 상품 없이도 존재한다 → `product → seller` 방향이 자연스럽다.
- **DB 구조와 일치**: 1:N 관계의 FK 는 원래 N 쪽(`products.seller_id`)에 있다.
- **무한히 커지는 컬렉션 방지**: 판매자가 상품 목록을 들면 상품이 늘수록 Seller 가 무거워진다. 필요하면 `ProductRepository.findBySellerId()` 로 조회한다.

### 경계별 스냅샷 필요 여부 (도전)

| 경계 | 참조 | 스냅샷 | 이유 |
|---|---|---|---|
| 주문 → 상품 | `OrderItem.productId` | **필요** (상품명·가격) | 주문은 "그때 그 가격으로 산" 거래 기록이다. 상품 가격·이름이 바뀌거나 상품이 삭제돼도 주문 내역·결제 금액·환불 기준은 바뀌면 안 된다. |
| 장바구니 → 상품 | `CartItem.productId` | 불필요 | 장바구니는 아직 확정되지 않은 "의도"다. 가격은 결제 시점의 현재 가격이 맞고, 담아둔 시점 가격을 저장하면 오히려 틀린 금액을 보여준다. |
| 주문 → 유저 | `Order.userId` | 선택적 (배송지·수령인) | 회원 정보 자체는 최신이면 충분하지만, 배송지·수령인 연락처는 주문 시점 값이 보존돼야 한다. 지금 모델엔 배송지가 없어 ID 만 둔다. |
| 주문 → 결제 | `Order.paymentId` | 불필요 | 결제는 금액·상태를 스스로 보관하는 독립된 기록이다. 결제 금액은 `Payment.amount` 에 이미 확정 저장된다. |
| 장바구니 → 유저 | `Cart.userId` | 불필요 | 장바구니 주인이 누구인지만 알면 된다. |
| 판매자 → 유저 | `Seller.userId` | 불필요 | 계정 연결 관계일 뿐, 시점 보존이 필요한 값이 없다. |
| 상품 → 판매자 | `Product.sellerId` | 불필요 | 판매자 상호 등은 항상 최신이 맞다. (단, 주문 시 판매자 상호까지 보존해야 한다면 주문 쪽 스냅샷에 추가) |

> 기준: **"참조 대상이 나중에 바뀌었을 때, 내 기록도 따라 바뀌면 안 되는가?"** — 그렇다면 스냅샷이 필요하다.

## 완료 기준 — 검증 테스트

```bash
./gradlew test --tests '*DomainBoundaryTest*'
```

`src/test/java/.../homework/DomainBoundaryTest.java` 가 모든 Entity 를 스캔해 **다른 도메인 Entity 를 JPA 연관관계로 들고 있는 필드**를 찾아냅니다.
지금 실행하면 아래처럼 끊어야 할 8곳이 실패 메시지로 나옵니다. 0개가 되면 통과입니다.

```
Cart.user → User (cart 도메인 → user 도메인)
CartItem.product → Product (cart 도메인 → product 도메인)
Order.user → User ...
```

기존 `OrderRepositoryTests` 도 함께 통과해야 합니다 (`./gradlew test`).

## 제출 방법

1. 이 레포를 **fork** 하고, 본인 fork 에서 이 브랜치를 기준으로 `hw/ddd-1` 브랜치를 만듭니다.
   ```bash
   git fetch upstream && git checkout -b hw/ddd-1 upstream/homework/ddd-1
   ```
2. **마감(11/18 (수) 13:30) 전까지** 본인 fork 의 브랜치(또는 PR) 링크를 과제 제출 채널에 올립니다.
3. 14:00 수업은 **과제 리뷰 15분**으로 시작합니다. 2~3명의 코드를 정답 브랜치와 나란히 비교합니다.

> 막히면 정답 브랜치 `lectures/ddd/step2-done` 와 `git diff` 로 비교해 봐도 됩니다. 단, **왜 그렇게 바뀌었는지 설명할 수 있어야** 합니다.
