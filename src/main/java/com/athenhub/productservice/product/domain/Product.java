package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.exception.ProductDomainErrorCode.*;

import com.athenhub.productservice.global.domain.AbstractAuditEntity;
import com.athenhub.productservice.product.domain.dto.UpdateVariantCommand;
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
 *   <li>동일한 옵션(Color + Size 조합)은 둘 이상 존재할 수 없다.
 *   <li>옵션 삭제는 Soft Delete 방식으로 처리된다.
 *   <li>ProductVariant는 Aggregate Root(Product)를 통해서만 변경 가능하다.
 * </ul>
 *
 * <p>■ 상태 / 타입
 *
 * <ul>
 *   <li>{@link ProductStatus}: 상품의 판매/준비/품절 상태 관리
 *   <li>{@link ProductType}: 옵션 상품/단일 상품 여부
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

  /** 상품 기본 가격 */
  @Embedded private Price price;

  /**
   * 상품의 옵션 목록.
   *
   * <p>cascade = ALL + orphanRemoval = true : Product가 Aggregate Root이므로 Child Entity는 Product
   * 생명주기를 따른다.
   */
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductVariant> variants = new ArrayList<>();

  /** 상품 판매 상태 */
  @Enumerated(EnumType.STRING)
  private ProductStatus status;

  /** 상품 유형 (옵션상품인지 여부) */
  @Enumerated(EnumType.STRING)
  private ProductType type;

  /**
   * 상품 생성자 (도메인 내부 전용)
   *
   * @param id ProductId
   * @param hubId 허브 정보
   * @param vendorId 공급사 ID
   * @param price 기본 가격
   * @param type 상품 타입
   */
  private Product(ProductId id, HubId hubId, VendorId vendorId, Price price, ProductType type) {
    this.id = id;
    this.hubId = hubId;
    this.vendorId = vendorId;
    this.price = price;
    this.status = ProductStatus.DRAFT; // 최초 생성 시 기본 상태
    this.type = type;
  }

  /**
   * 상품 정적 팩토리 메서드.
   *
   * <p>외부에서 Product 생성을 의도적으로 제한하고 도메인 생성 규칙을 강제한다.
   */
  public static Product create(HubId hubId, VendorId vendorId, Price price, ProductType type) {
    return new Product(ProductId.create(), hubId, vendorId, price, type);
  }

  /**
   * 옵션 수정.
   *
   * @param variantId 수정할 옵션의 ID
   * @param command 색상/사이즈 변경 요청 정보
   */
  public void updateVariant(ProductVariantId variantId, UpdateVariantCommand command) {
    ensureOptionType();
    getVariant(variantId).update(command.color(), command.size());
  }

  /**
   * 상품 기본 정보 변경.
   *
   * <p>허브/공급사/기본가격 변경
   */
  public void updateBasic(HubId hubId, VendorId vendorId, Price price) {
    this.hubId = hubId;
    this.vendorId = vendorId;
    this.price = price;
  }

  /**
   * 상품 유형 변경 (OPTION / NORMAL 등)
   *
   * <p>도메인 규칙이 필요하다면 ensure 로직 추가 가능
   */
  public void updateType(ProductType type) {
    this.type = type;
  }

  /**
   * 상품 상태 변경 (DRAFT → READY → SELL → SOLD_OUT ...)
   *
   * <p>상태 전이 규칙이 필요하면 여기에서 검증 가능
   */
  public void updateStatus(ProductStatus status) {
    this.status = status;
  }

  /**
   * 옵션 추가.
   *
   * <p>■ 도메인 규칙
   *
   * <ul>
   *   <li>옵션 상품(ProductType.OPTION)이 아니면 추가할 수 없다.
   *   <li>이미 존재하는 옵션(Color + Size 조합)은 추가할 수 없다.
   * </ul>
   *
   * @param addVariant 추가할 옵션
   */
  public void addVariant(ProductVariant addVariant) {
    ensureOptionType();
    ensureVariantNotExists(addVariant);
    addVariant.assignTo(this);
    this.variants.add(addVariant);
  }

  /**
   * 옵션 삭제 (Soft Delete).
   *
   * @param variantId 삭제할 옵션 ID
   * @param username 삭제 수행자
   */
  public void removeVariant(ProductVariantId variantId, String username) {
    ensureOptionType();
    getVariant(variantId).delete(username);
  }

  /**
   * 옵션 목록 조회.
   *
   * @param activeOnly true면 삭제되지 않은 옵션만 반환, false면 전체 반환
   */
  public List<ProductVariant> getVariants(boolean activeOnly) {
    if (!activeOnly) {
      return List.copyOf(variants);
    }
    return variants.stream().filter(v -> !v.isDeleted()).toList();
  }

  /**
   * 옵션 단건 조회 (옵션이 없으면 예외)
   *
   * @param variantId 조회할 옵션 ID
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
   * 도메인 규칙: 옵션 상품인지 검증.
   *
   * <p>OPTION 타입이 아닌 상품은 옵션 기능을 사용할 수 없다.
   *
   * @throws ProductVariantNotSupportedException
   */
  private void ensureOptionType() {
    if (this.type != ProductType.OPTION) {
      throw new ProductVariantNotSupportedException(PRODUCT_VARIANT_NOT_SUPPORTED, this.type);
    }
  }

  /**
   * 도메인 규칙: 동일 옵션(Color + Size)이 이미 존재하는지 검사.
   *
   * @param addVariant 추가하려는 옵션
   * @throws VariantAlreadyExistsException 이미 동일 옵션이 존재할 경우
   */
  private void ensureVariantNotExists(ProductVariant addVariant) {
    if (variants.stream().anyMatch(v -> v.isSameOption(addVariant))) {
      throw new VariantAlreadyExistsException(PRODUCT_VARIANT_ALREADY_EXIST);
    }
  }
}
