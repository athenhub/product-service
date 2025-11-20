package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.vo.*;
import java.util.UUID;

/**
 * 테스트 Fixture 유틸리티 클래스.
 *
 * <p>Product 및 ProductVariant 도메인 객체를 테스트 목적으로 쉽게 생성할 수 있는 헬퍼 메서드들을 제공합니다. 이는 테스트의 Arrange (준비) 단계를
 * 간결하게 만듭니다.
 */
public class ProductFixture {

  /**
   * 주어진 생성 커맨드를 사용하여 Product 객체를 생성하고, 추가로 ProductVariant들을 포함하여 완전한 Product 객체를 생성합니다.
   *
   * @param productCreateRequest 기본 상품 생성을 위한 커맨드 객체
   * @param productVariantCreateRequest 상품에 추가할 Variant(옵션) 생성을 위한 커맨드 배열
   * @return 생성된 Product 도메인 객체
   */
  public static Product createProduct(
      ProductCreateCommand productCreateRequest,
      ProductVariantCreateCommand... productVariantCreateRequest) {
    Product product = Product.create(productCreateRequest);
    for (ProductVariantCreateCommand variantCreateRequest : productVariantCreateRequest) {
      product.addVariant(ProductVariant.create(variantCreateRequest));
    }
    return product;
  }

  /**
   * ProductType이 OPTION인 상품 객체를 기본값으로 생성합니다.
   *
   * @return ProductType.OPTION을 가진 Product 객체
   */
  public static Product createOptionProduct() {
    return Product.create(newProductCreateCommand(ProductType.OPTION));
  }

  /**
   * ProductType이 SIMPLE인 상품 객체를 기본값으로 생성합니다.
   *
   * @return ProductType.SIMPLE을 가진 Product 객체
   */
  public static Product createSimpleProduct() {
    return Product.create(newProductCreateCommand(ProductType.SIMPLE));
  }

  /**
   * 주어진 색상(color)과 크기(size)를 가지는 ProductVariant 객체를 생성합니다.
   *
   * @param color 상품 옵션의 색상 문자열
   * @param size 상품 옵션의 크기 문자열
   * @return 생성된 ProductVariant 도메인 객체
   */
  public static ProductVariant createProductVariant(String color, String size) {
    return ProductVariant.create(
        new ProductVariantCreateCommand(ProductColor.of(color), ProductSize.of(size)));
  }

  /**
   * 기본값이 채워진 ProductCreateCommand 객체를 생성합니다. HubId, VendorId는 랜덤 UUID를 사용하며, 가격은 10,000L입니다.
   *
   * @param type 생성할 상품의 타입 (SIMPLE, OPTION 등)
   * @return ProductCreateCommand DTO 객체
   */
  public static ProductCreateCommand newProductCreateCommand(ProductType type) {
    return new ProductCreateCommand(
        HubId.of(UUID.randomUUID()), VendorId.of(UUID.randomUUID()), Price.of(10_000L), type);
  }

  /**
   * 주어진 색상(color)과 크기(size)를 가지는 ProductVariantCreateCommand 객체를 생성합니다.
   *
   * @param color 상품 옵션의 색상 문자열
   * @param size 상품 옵션의 크기 문자열
   * @return ProductVariantCreateCommand DTO 객체
   */
  public static ProductVariantCreateCommand newProductVariantCreateCommand(
      String color, String size) {
    return new ProductVariantCreateCommand(ProductColor.of(color), ProductSize.of(size));
  }
}
