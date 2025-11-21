package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.exception.ProductDomainErrorCode.*;

import com.athenhub.productservice.global.domain.AbstractAuditEntity;
import com.athenhub.productservice.product.domain.dto.*;
import com.athenhub.productservice.product.domain.exception.ProductVariantNotSupportedException;
import com.athenhub.productservice.product.domain.vo.*;
import jakarta.persistence.*;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Product Aggregate Root.
 *
 * <p>상품(Product)은 옵션(ProductVariant)을 포함하는 Aggregate Root로서, 옵션(Variant)에 대한 생성/수정/삭제는 반드시 Product를
 * 통해서만 수행해야 한다.
 *
 * <p>본 클래스는 다음과 같은 도메인 규칙을 보장한다:
 *
 * <ul>
 *   <li>상품 종류({@link ProductType})가 OPTION이 아닌 경우 옵션 조작이 불가하다.
 *   <li>동일한 옵션(Color + Size 조합)은 중복 생성될 수 없다.
 *   <li>옵션 삭제는 Soft Delete로 처리되며 Product의 삭제와 함께 전파된다.
 *   <li>Variant는 Product Aggregate Root의 생명주기에 종속되며 외부에서 독립적으로 관리될 수 없다.
 * </ul>
 *
 * <p>옵션 컬렉션은 {@link ProductVariants}라는 1급 컬렉션(First-Class Collection)으로 관리되며, 이는 옵션 관련 도메인 규칙을
 * Product로부터 분리하여 응집도를 높이고 Aggregate Root를 더 작고 명확하게 유지한다.
 *
 * <p>Product 자체는 상태/타입/가격/Hub/Vendor와 같은 '상품 그 자체'의 불변식을 관리하며, 옵션 리스트의 상세 로직은 ProductVariants가
 * 책임진다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Entity
@Table(name = "p_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends AbstractAuditEntity {

  /** 상품 식별자 (Aggregate Identifier). */
  @EmbeddedId private ProductId id;

  /** 상품이 속한 허브 정보. */
  @Embedded private HubId hubId;

  /** 상품 공급사 정보. */
  @Embedded private VendorId vendorId;

  /** 기본 판매 가격. */
  @Embedded private Price price;

  /**
   * 상품 옵션 목록.
   *
   * <p>옵션(ProductVariant)의 컬렉션은 단순 List가 아니라 도메인 규칙을 포함한 1급 컬렉션 {@link ProductVariants}로 감싼다.
   *
   * <p>이 컬렉션은 Cascade + OrphanRemoval을 내부에 포함하며, Product Aggregate Root의 생명주기와 일치하도록 옵션을 관리한다.
   */
  @Embedded private ProductVariants variants = new ProductVariants();

  /** 상품의 판매/노출 상태. 예: DRAFT, ON_SALE, SOLD_OUT 등 */
  @Enumerated(EnumType.STRING)
  private ProductStatus status;

  /** 상품 유형 (NORMAL / OPTION). */
  @Enumerated(EnumType.STRING)
  private ProductType type;

  /**
   * Product 생성 팩토리 메서드.
   *
   * <p>Product는 생성 시:
   *
   * <ul>
   *   <li>식별자 자동 생성
   *   <li>DRAFT 상태로 초기화
   *   <li>옵션은 초기 상태에서 비어있음
   * </ul>
   *
   * <p>영속화(save) 책임은 Repository 계층에서 담당한다.
   */
  public static Product create(ProductCreateCommand command) {
    Product product = new Product();
    product.id = ProductId.create();
    product.hubId = command.hubId();
    product.vendorId = command.vendorId();
    product.price = command.price();
    product.type = command.type();
    product.status = ProductStatus.DRAFT;
    return product;
  }

  /**
   * 옵션 목록 조회.
   *
   * <p>외부에서 옵션 리스트를 직접 변경할 수 없도록 {@code immutable list}로 반환한다.
   */
  public List<ProductVariant> getVariants() {
    return variants.getValues();
  }

  /** 상품 기본 정보 변경. */
  public void updateBasic(ProductBasicUpdateCommand command) {
    this.hubId = command.hubId();
    this.vendorId = command.vendorId();
    this.price = command.price();
  }

  /** 상품 유형 변경 (NORMAL ↔ OPTION). */
  public void changeType(ProductType type) {
    this.type = type;
  }

  /** 상품 상태 변경. */
  public void changeStatus(ProductStatus status) {
    this.status = status;
  }

  /**
   * 옵션 추가.
   *
   * <p>도메인 규칙:
   *
   * <ul>
   *   <li>상품 유형이 OPTION이 아니면 추가 불가
   * </ul>
   *
   * @return 생성된 옵션의 식별자
   */
  public ProductVariantId addVariant(ProductVariantCreateCommand command) {
    ensureOptionType();
    ProductVariant variant = ProductVariant.create(command);
    variants.add(variant, this);
    return variant.getId();
  }

  /**
   * 옵션 수정.
   *
   * <p>Variant를 조회하고 해당 엔티티가 직접 update를 수행한다.
   */
  public void updateVariant(ProductVariantUpdateCommand command) {
    ensureOptionType();
    variants.update(command);
  }

  /** 옵션 삭제(Soft Delete). */
  public void removeVariant(ProductVariantRemoveCommand command) {
    ensureOptionType();
    variants.remove(command.productVariantId(), command.username());
  }

  /**
   * 옵션 일괄 처리(추가/수정/삭제).
   *
   * <p>VariantUpdateSet은 하나의 요청으로 다양한 옵션 조작을 수행하기 위한 도메인 객체이며, ProductVariants가 이를 적용한다.
   */
  public void apply(VariantUpdateSet updateSet) {
    ensureOptionType();
    variants.apply(updateSet, this);
  }

  /**
   * 상품 삭제.
   *
   * <p>Soft Delete로 처리되며, 옵션들도 함께 삭제된다.
   */
  @Override
  public void delete(String deleteBy) {
    super.delete(deleteBy);
    this.status = ProductStatus.REMOVED;
    variants.removeAll(deleteBy);
  }

  /**
   * 도메인 규칙: 상품 유형이 옵션 상품인지 검사.
   *
   * <p>OPTION 상품이 아니면 옵션 관련 작업은 모두 금지된다.
   */
  private void ensureOptionType() {
    if (this.type != ProductType.OPTION) {
      throw new ProductVariantNotSupportedException(PRODUCT_VARIANT_NOT_SUPPORTED);
    }
  }
}
