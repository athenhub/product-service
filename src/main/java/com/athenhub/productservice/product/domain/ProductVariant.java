package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.global.domain.AbstractAuditEntity;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ProductVariant (상품 옵션).
 *
 * <p>색상(Color), 사이즈(Size) 등 "옵션 하나"를 나타내는 도메인 엔티티이다.
 *
 * <p>■ Aggregate 규칙
 *
 * <ul>
 *   <li>ProductVariant는 독립적으로 조작될 수 없고 반드시 Product Aggregate Root를 통해서만 변경된다.
 *   <li>Variant 생성/삭제/수정은 Product가 통제한다.
 *   <li>ProductVariant는 Soft Delete(Audit 기반 삭제)를 지원한다.
 * </ul>
 *
 * <p>■ 도메인 속성
 *
 * <ul>
 *   <li>{@link ProductColor} — 옵션의 색상
 *   <li>{@link ProductSize} — 옵션의 사이즈
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Entity
@Table(name = "p_product_variant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductVariant extends AbstractAuditEntity {

  /** 상품이 옵션을 가지지 않는 경우를 표현하며 "없음(NONE)"의 의미를 가진다. */
  private static String DEFAULT_COLOR = "NONE";

  /** 상품이 옵션을 가지지 않는 경우를 표현하며 "없음(NONE)"의 의미를 가진다. */
  private static String DEFAULT_SIZE = "NONE";

  /** 옵션 ID (Aggregate 내부 식별자). */
  @EmbeddedId private ProductVariantId id;

  /**
   * 이 옵션이 속한 상품 (Aggregate Root).
   *
   * <p>옵션의 생명주기는 Product 생명주기를 따른다.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  /** 옵션 색상. */
  @Embedded private ProductColor color;

  /** 옵션 사이즈. */
  @Embedded private ProductSize size;

  /**
   * 옵션 생성 팩토리 메서드.
   *
   * <p>외부에서는 Product를 통해 추가되므로 이 메서드는 Product 내부에서 사용된다.
   */
  public static ProductVariant create(ProductVariantCreateCommand request) {
    ProductVariant productVariant = new ProductVariant();
    productVariant.id = ProductVariantId.create();
    productVariant.color = request.color();
    productVariant.size = request.size();
    return productVariant;
  }

  /**
   * SIMPLE(NORMAL) 타입 상품을 위한 기본(Default) 옵션을 생성한다.
   *
   * <p>이 메서드는 색상과 사이즈 개념이 없는 단일 상품을 표현하기 위해 사용되며, 다음과 같은 특징을 가진다:
   *
   * <ul>
   *   <li>{@link ProductColor} 는 {@code NONE} 으로 설정된다
   *   <li>{@link ProductSize} 는 {@code NONE} 으로 설정된다
   *   <li>이 Variant는 "실제 옵션"이 아닌, <b>논리적 기본값(Default)</b>을 의미한다
   * </ul>
   *
   * <p>이 메서드는 {@code Product.create(...)} 또는 {@code ProductVariants.createDefault(...)} 내부에서만 사용되는
   * 것을 전제로 한다. 즉, 외부 애플리케이션 레이어에서 직접 사용하지 않는다.
   *
   * @return 기본 옵션을 나타내는 ProductVariant
   * @author 김지원
   * @since 1.0.0
   */
  static ProductVariant createDefault() {
    ProductVariant productVariant = new ProductVariant();
    productVariant.id = ProductVariantId.create();
    productVariant.color = ProductColor.of(DEFAULT_COLOR);
    productVariant.size = ProductSize.of(DEFAULT_SIZE);
    return productVariant;
  }

  /**
   * 옵션 정보 변경 (색상/사이즈).
   *
   * <p>도메인 규칙: 반드시 Product Aggregate Root를 통해 호출되어야 한다.
   */
  public void update(ProductColor color, ProductSize size) {
    this.color = color;
    this.size = size;
  }

  /**
   * 이 옵션과 다른 옵션이 동일한 Color + Size 조합인지 비교한다.
   *
   * @param variant 비교 대상 옵션
   * @return true면 옵션 값이 동일함
   */
  public boolean isSameOption(ProductVariant variant) {
    return this.color.equals(variant.color) && this.size.equals(variant.size);
  }

  /**
   * 해당 옵션을 특정 상품(Product)에 연결한다.
   *
   * <p>옵션은 Product 외부에서 존재할 수 없으므로 반드시 Aggregation을 위한 연결이 필요하다.
   */
  public void assignTo(Product product) {
    this.product = product;
  }

  /**
   * Soft Delete 여부 확인.
   *
   * <p>Audit 필드 (deletedAt, deletedBy)를 활용해 삭제 상태를 판단한다.
   */
  public boolean isDeleted() {
    return this.getDeletedAt() != null || this.getDeletedBy() != null;
  }
}
