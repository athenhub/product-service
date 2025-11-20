package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 상품 옵션(ProductVariant)의 고유 식별자를 나타내는 값 객체(Value Object).
 *
 * <p>ProductVariantId는 단순한 UUID가 아니라, “상품 옵션이라는 도메인 개념을 명확하게 식별하는 전용 타입”으로 사용된다. 이를 통해 타입
 * 안전성(type-safety)을 확보하고, 다른 ID와의 혼동을 방지한다.
 *
 * <p>■ 특징
 *
 * <ul>
 *   <li>UUID 기반 불변 값 객체
 *   <li>JPA Value Type 으로 활용 (@Embeddable)
 *   <li>정적 팩토리 메서드(of, create)로만 생성 가능
 * </ul>
 *
 * <p>■ 생성 방식
 *
 * <ul>
 *   <li>{@link #of(UUID)} — 외부에서 전달된 UUID를 래핑하여 생성
 *   <li>{@link #create()} — 새로운 UUID를 생성하여 ProductVariantId를 생성
 * </ul>
 *
 * <p>■ 사용 예
 *
 * <pre>
 * ProductVariantId id = ProductVariantId.create();
 * UUID raw = id.toUuid();
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ProductVariantId {

  /** 상품 옵션의 고유 UUID. */
  private UUID id;

  /**
   * UUID 값을 반환한다.
   *
   * @return 옵션 식별자(UUID)
   */
  public UUID toUuid() {
    return id;
  }

  /** 내부 생성자 — 외부 생성을 제한하고 정적 팩토리 사용을 강제한다. */
  private ProductVariantId(UUID id) {
    this.id = id;
  }

  /**
   * 주어진 UUID로 ProductVariantId 생성.
   *
   * @param uuid 옵션 ID(UUID)
   * @return ProductVariantId
   */
  public static ProductVariantId of(UUID uuid) {
    return new ProductVariantId(uuid);
  }

  /**
   * 새로운 UUID를 생성하여 ProductVariantId 생성.
   *
   * @return 새로운 ProductVariantId 인스턴스
   */
  public static ProductVariantId create() {
    return new ProductVariantId(UUID.randomUUID());
  }
}
