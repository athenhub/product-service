package com.athenhub.productservice.product.application.dto;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductStatus;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.ProductVariant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 상품 상세 정보를 응답하기 위한 DTO.
 *
 * <p>Product Aggregate의 주요 정보를 외부 계층(Controller / API)에 전달하기 위해 사용하는 조회 전용(Read Model) 객체이다.
 *
 * <p>도메인 엔티티 {@link Product}를 직접 노출하지 않고, 필요한 정보만 추출하여 불변 객체(Record) 형태로 반환한다.
 *
 * <p>Variant(옵션)가 존재하는 상품의 경우, 각 옵션은 {@link ProductVariantDetails}로 변환되어 포함된다.
 *
 * <p>재고(quantity)는 향후 별도 Inventory 서비스와 연동한 후 확장될 예정이다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductDetail(
    UUID productId,
    String name,
    String description,
    Long price,
    UUID hubId,
    UUID vendorId,
    ProductType type,
    ProductStatus status,
    List<ProductVariantDetails> variants,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt) {

  /**
   * 상품 옵션(Variant) 상세 정보를 위한 DTO.
   *
   * <p>하나의 ProductVariant 엔티티를 외부 계층에서 사용 가능한 형태로 변환한다.
   *
   * <ul>
   *   <li>variantId : 옵션 식별자
   *   <li>color : 색상
   *   <li>size : 사이즈
   *   <li>quantity : 재고 (현재는 0으로 고정)
   * </ul>
   *
   * @author 김지원
   * @since 1.0.0
   */
  public record ProductVariantDetails(UUID variantId, String color, String size, int quantity) {

    /**
     * 도메인 엔티티 {@link ProductVariant}를 {@link ProductVariantDetails}로 변환한다.
     *
     * @param variant 변환할 ProductVariant
     * @return 옵션 상세 DTO
     */
    public static ProductVariantDetails from(ProductVariant variant) {
      return new ProductVariantDetails(
          variant.getId().toUuid(),
          variant.getColor().getValue(),
          variant.getSize().getValue(),
          0 // TODO: 재고 서비스 연동 후 실제 재고 반영
          );
    }
  }

  /**
   * 도메인 엔티티 {@link Product}를 {@link ProductDetail}로 변환한다.
   *
   * <p>Aggregate Root인 Product로부터 필요한 값만 추출하여 응답용 DTO로 매핑한다.
   *
   * <p>옵션 정보는 {@link Product#getVariants()}를 통해 조회되며, 각 Variant는 {@link
   * ProductVariantDetails#from(ProductVariant)}를 통해 변환된다.
   *
   * @param product 변환할 Product 엔티티
   * @return 상품 상세 DTO
   */
  public static ProductDetail from(Product product) {
    return new ProductDetail(
        product.getId().toUuid(),
        product.getName(),
        product.getDescription(),
        product.getPrice().value(),
        product.getHubId().toUuid(),
        product.getVendorId().toUuid(),
        product.getType(),
        product.getStatus(),
        product.getVariants().stream()
            .map(ProductVariantDetails::from)
            .collect(Collectors.toList()),
        product.getCreatedAt(),
        product.getUpdatedAt(),
        product.getDeletedAt());
  }
}
