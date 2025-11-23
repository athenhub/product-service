package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductStatus;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.ProductVariant;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 상품 상세 정보를 응답하기 위한 DTO
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductDetails(
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
    // TODO quantity 재고 서비스를 만든 후 추가 예정
  public record ProductVariantDetails(UUID variantId, String color, String size, int quantity) {

    public static ProductVariantDetails from(ProductVariant variant) {
      return new ProductVariantDetails(
          variant.getId().toUuid(), variant.getColor().getValue(), variant.getSize().getValue(), 0);
    }
  }

  public static ProductDetails from(Product product) {
    return new ProductDetails(
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
