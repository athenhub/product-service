package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.exception.ProductDomainErrorCode.*;

import com.athenhub.productservice.global.domain.AbstractAuditEntity;
import com.athenhub.productservice.product.domain.dto.ProductBasicUpdateRequest;
import com.athenhub.productservice.product.domain.dto.ProductCreateRequest;
import com.athenhub.productservice.product.domain.dto.ProductVariantRemoveRequest;
import com.athenhub.productservice.product.domain.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.domain.exception.ProductVariantNotSupportedException;
import com.athenhub.productservice.product.domain.exception.VariantAlreadyExistsException;
import com.athenhub.productservice.product.domain.exception.VariantNotFoundException;
import com.athenhub.productservice.product.domain.vo.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Product Aggregate Root.
 *
 * <p>상품(Product)은 옵션(ProductVariant)을 포함하는 Aggregate Root이며, 옵션 생성/수정/삭제는 반드시 Product를 통해서만 수행해야
 * 한다.
 *
 * <p>■ 주요 도메인 규칙
 *
 * <ul>
 *   <li>상품 타입(ProductType)이 OPTION이 아닌 경우 옵션을 추가/수정/삭제할 수 없다.
 *   <li>동일한 옵션(Color + Size 조합)은 중복 생성될 수 없다.
 *   <li>옵션 삭제는 Soft Delete 방식으로 처리된다.
 *   <li>옵션(ProductVariant)은 항상 Product의 생명주기를 따른다.
 * </ul>
 *
 * <p>■ 상태 / 타입
 *
 * <ul>
 *   <li>{@link ProductStatus}: 상품의 판매/준비/품절 상태
 *   <li>{@link ProductType}: 옵션 상품 여부 (NORMAL / OPTION)
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Entity
@Table(name = "p_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends AbstractAuditEntity {

  /** 상품 ID (Aggregate Identifier) */
  @EmbeddedId private ProductId id;

  /** 허브 정보 */
  @Embedded private HubId hubId;

  /** 공급사 정보 */
  @Embedded private VendorId vendorId;

  /** 기본 판매 가격 */
  @Embedded private Price price;

  /**
   * 상품 옵션 목록.
   *
   * <p>cascade = ALL + orphanRemoval = true: Product가 Aggregate Root이기 때문에 Child
   * Entity(ProductVariant)의 생명주기는 Product가 관리한다.
   */
  @Getter(AccessLevel.NONE)
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductVariant> variants = new ArrayList<>();

  /** 상품 상태 (예: DRAFT, ON_SALE, SOLD_OUT 등) */
  @Enumerated(EnumType.STRING)
  private ProductStatus status;

  /** 상품 유형 (단일상품 / 옵션상품) */
  @Enumerated(EnumType.STRING)
  private ProductType type;

  /**
   * 상품 생성 정적 팩토리 메서드.
   *
   * <p>외부에서 생성자를 직접 호출하지 못하도록 하고, 도메인 규칙을 강제하는 유일한 생성 지점이다.
   *
   * @param request 상품 생성 요청 정보
   * @return 생성된 Product
   */
  public static Product create(ProductCreateRequest request) {
    Product product = new Product();
    product.id = ProductId.create();
    product.hubId = HubId.of(request.hubId());
    product.vendorId = VendorId.of(request.vendorId());
    product.price = Price.of(request.price());
    product.type = request.type();
    product.status = ProductStatus.DRAFT; // 최초 생성 시 기본 상태
    return product;
  }

  /**
   * 상품에 포함된 옵션(Variant) 목록을 반환한다.
   *
   * <p>반환되는 리스트는 불변(Immutable) 리스트
   *
   * <p>주의:
   *
   * <ul>
   *   <li>리스트 자체는 불변이지만, 각 Variant 엔티티는 변경 가능한 객체이다.
   *   <li>Option 추가/삭제 등 컬렉션 변경은 반드시 Product Aggregate Root 메서드를 통해서만 가능하다.
   * </ul>
   *
   * @return 옵션 목록의 불변 리스트
   */
  public List<ProductVariant> getVariants() {
    return List.copyOf(variants);
  }

  /**
   * 상품 기본 정보 변경(허브/공급사/기본가격).
   *
   * @param request 수정 요청 정보
   */
  public void updateBasic(ProductBasicUpdateRequest request) {
    this.hubId = HubId.of(request.hubId());
    this.vendorId = VendorId.of(request.vendorId());
    this.price = Price.of(request.price());
  }

  /** 상품 유형 변경 (NORMAL ↔ OPTION) */
  public void changeType(ProductType type) {
    this.type = type;
  }

  /**
   * 상품 상태 변경 (DRAFT → ON_SALE, SOLD_OUT 등).
   *
   * <p>TODO 상태 전이 규칙이 필요하면 이곳에서 처리한다.
   */
  public void changeStatus(ProductStatus status) {
    this.status = status;
  }

  /**
   * 옵션 추가.
   *
   * <p>■ 도메인 규칙
   *
   * <ul>
   *   <li>상품 타입이 OPTION이 아니면 추가 불가
   *   <li>동일한 옵션(Color + Size)이 존재하면 추가 불가
   * </ul>
   *
   * @param addVariant 추가할 옵션
   * @throws ProductVariantNotSupportedException 옵션 상품이 아닌 경우
   * @throws VariantAlreadyExistsException 동일 옵션이 이미 존재하는 경우
   */
  public void addVariant(ProductVariant addVariant) {
    ensureOptionType();
    ensureVariantNotExists(addVariant);

    addVariant.assignTo(this); // 연관관계 설정
    this.variants.add(addVariant);
  }

  /**
   * 옵션 정보 수정.
   *
   * @param request 옵션 수정 요청 정보 (색상/사이즈)
   * @throws ProductVariantNotSupportedException 옵션 상품이 아닌 경우
   * @throws VariantNotFoundException 대상 옵션이 없는 경우
   */
  public void updateVariant(ProductVariantUpdateRequest request) {
    ensureOptionType();
    getVariant(ProductVariantId.of(request.productVariantId()))
        .update(request.color(), request.size());
  }

  /**
   * 옵션 삭제 (Soft Delete).
   *
   * @param request 삭제 요청 정보
   * @throws ProductVariantNotSupportedException 옵션 상품이 아닌 경우
   * @throws VariantNotFoundException 해당 옵션이 없는 경우
   */
  public void removeVariant(ProductVariantRemoveRequest request) {
    ensureOptionType();
    getVariant(ProductVariantId.of(request.productVariantId())).delete(request.username());
  }

  /**
   * 옵션 목록 조회.
   *
   * @param activeOnly true면 삭제되지 않은 옵션만 조회, false면 전체 조회
   */
  public List<ProductVariant> getVariants(boolean activeOnly) {
    if (!activeOnly) {
      return getVariants();
    }
    return variants.stream().filter(v -> !v.isDeleted()).toList();
  }

  /**
   * 옵션 단건 조회 (없으면 예외 발생)
   *
   * @param variantId 옵션 ID
   * @return ProductVariant
   * @throws VariantNotFoundException 옵션이 존재하지 않을 경우
   */
  private ProductVariant getVariant(ProductVariantId variantId) {
    return variants.stream()
        .filter(v -> v.getId().equals(variantId))
        .findFirst()
        .orElseThrow(
            () ->
                new VariantNotFoundException(
                    PRODUCT_VARIANT_NOT_FOUND, id.toUuid(), variantId.toUuid()));
  }

  /**
   * 도메인 규칙: 옵션 상품 여부 검증.
   *
   * @throws ProductVariantNotSupportedException 옵션 상품이 아닌 경우
   */
  private void ensureOptionType() {
    if (this.type != ProductType.OPTION) {
      throw new ProductVariantNotSupportedException(PRODUCT_VARIANT_NOT_SUPPORTED, this.type);
    }
  }

  /**
   * 도메인 규칙: 동일 옵션(Color + Size) 중복 여부 검사.
   *
   * @param addVariant 추가하려는 옵션
   * @throws VariantAlreadyExistsException 동일 조합이 이미 존재할 경우
   */
  private void ensureVariantNotExists(ProductVariant addVariant) {
    if (variants.stream().anyMatch(v -> v.isSameOption(addVariant))) {
      throw new VariantAlreadyExistsException(PRODUCT_VARIANT_ALREADY_EXIST);
    }
  }
}
