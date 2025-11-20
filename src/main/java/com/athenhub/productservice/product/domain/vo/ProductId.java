package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 상품(Product)의 고유 식별자를 표현하는 값 객체(Value Object).
 *
 * <p>ProductId는 단순 UUID를 감싸는 것이 아니라, “상품이라는 도메인을 명확하게 식별하는 전용 타입”으로 사용된다. 이는 잘못된 타입의 ID 사용을 방지하고,
 * 도메인의 명확성과 안전성을 높인다.
 *
 * <p>■ 특징
 *
 * <ul>
 *   <li>UUID 기반의 불변 값 객체
 *   <li>@Embeddable 을 사용하여 JPA Value Type 으로 매핑
 *   <li>정적 팩토리 메서드(of, create)를 통한 명확한 생성 방식 제공
 * </ul>
 *
 * <p>■ 생성 방식
 *
 * <ul>
 *   <li>{@link #of(UUID)} — 외부에서 전달된 UUID를 사용하여 생성
 *   <li>{@link #create()} — 새로운 UUID를 생성하여 ProductId를 생성
 * </ul>
 *
 * <p>■ 예시
 *
 * <pre>
 * ProductId id = ProductId.create();
 * UUID raw = id.toUuid();  // UUID가 필요할 때
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ProductId {

  /** 상품의 고유 UUID. */
  private UUID id;

  /**
   * UUID 값을 그대로 반환한다.
   *
   * @return UUID 값
   */
  public UUID toUuid() {
    return id;
  }

  /**
   * 내부 생성자. 외부에서 직접 생성하지 못하도록 보호하며, 정적 팩토리 메서드 사용을 강제한다.
   *
   * @param id 값은 null 을 허용하지 않는다.
   */
  private ProductId(UUID id) {
    this.id = Objects.requireNonNull(id);
  }

  /**
   * 주어진 UUID로 ProductId를 생성한다.
   *
   * @param uuid 상품 식별자(UUID)
   * @return ProductId 인스턴스
   */
  public static ProductId of(UUID uuid) {
    return new ProductId(uuid);
  }

  /**
   * 새로운 UUID를 생성하여 ProductId를 만든다. 주로 신규 상품 생성 시 사용된다.
   *
   * @return 새로운 ProductId
   */
  public static ProductId create() {
    return new ProductId(UUID.randomUUID());
  }

  /** toString 오버라이드 — UUID 문자열로 표현한다. */
  @Override
  public String toString() {
    return id.toString();
  }
}
