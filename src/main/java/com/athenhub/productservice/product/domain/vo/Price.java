package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

/**
 * 금액(Price)을 표현하는 값 객체(Value Object)이다.
 *
 * <p>Price는 불변(Immutable)하며, 모든 연산(add, multiply 등)은 새로운 Price 인스턴스를 반환한다.
 *
 * <p>■ 도메인 특징
 *
 * <ul>
 *   <li>가격은 null이 될 수 없으며 JPA를 위한 protected 생성자만 null 허용
 *   <li>동일 금액일 경우 동일 객체로 판단(equals/hashCode 구현)
 *   <li>연산(add, multiply)은 불변성을 유지하도록 새로운 Price를 생성한다
 * </ul>
 *
 * <p>■ 사용 예
 *
 * <pre>
 * Price price = Price.of(1000L);
 * Price total = price.multiply(3); // 3000원
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Embeddable
@EqualsAndHashCode
public class Price {

  /** 금액 값. */
  @Column(name = "price_amount", nullable = false)
  private final Long amount;

  /** JPA 전용 생성자 (프록시/리플렉션용) 금액은 실제로 null이면 안 되지만 JPA에서는 초기화 과정에서 필요하다. */
  protected Price() {
    this.amount = null;
  }

  /** 내부 생성자. 금액은 절대 음수가 되어서는 안 되며, 필요한 경우 유효성 검증 확장 가능. */
  private Price(long amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Price.amount 값은 0 이상이여야 합니다.");
    }
    this.amount = amount;
  }

  /**
   * Price 정적 팩토리 메서드.
   *
   * @param amount 금액
   * @return Price 객체
   */
  public static Price of(long amount) {
    return new Price(amount);
  }

  /** Price 덧셈 연산. 두 금액을 더한 새로운 Price 객체를 반환한다. */
  public Price add(Price other) {
    return new Price(this.value() + other.value());
  }

  /** Price 곱셈 연산. 특정 수량과 곱한 Price를 반환한다. */
  public Price multiply(int quantity) {
    return new Price(this.value() * quantity);
  }

  /**
   * 내부 금액(long)을 반환한다.
   *
   * @throws IllegalStateException JPA 생성자에서 amount가 null일 경우
   */
  public long value() {
    if (amount == null) {
      throw new IllegalStateException("Price.amount 값이 null 값입니다.");
    }
    return amount;
  }
}
