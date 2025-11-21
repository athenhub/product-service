package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.product.application.service.policy.ProductCreatePermissionPolicy;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.vo.*;
import java.util.UUID;

/**
 * Product 도메인 테스트를 위한 Fixture 클래스이다.
 *
 * <p>테스트 코드에서 불필요한 빌더/생성 로직 중복을 줄이고, 도메인 객체(Product, ProductVariant, Command)를 간결하게 생성할 수 있도록 돕는 헬퍼
 * 메서드를 제공한다.
 *
 * <p>주로 다음 목적을 위해 사용된다.
 *
 * <ul>
 *   <li>도메인 테스트의 Arrange 단계 단순화
 *   <li>의미 있는 기본값 제공
 *   <li>권한 정책(PermissionPolicy)을 유연하게 테스트에 주입
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public class ProductFixture {

  /**
   * 전달받은 권한 정책을 사용하여 OPTION 타입 상품을 생성한다.
   *
   * <p>외부에서 주입한 {@link ProductCreatePermissionPolicy}를 통해 상품 생성 권한 체크 로직을 테스트 단위에서 자유롭게 제어할 수 있다.
   *
   * @return ProductType.OPTION을 가지는 Product 객체
   */
  public static Product createOptionProduct() {
    return Product.create(newProductCreateCommand(ProductType.OPTION));
  }

  /**
   * 전달받은 권한 정책을 사용하여 SIMPLE 타입 상품을 생성한다.
   *
   * @return ProductType.SIMPLE을 가지는 Product 객체
   */
  public static Product createSimpleProduct() {
    return Product.create(newProductCreateCommand(ProductType.SIMPLE));
  }

  /**
   * 주어진 색상과 사이즈로 ProductVariant 객체를 생성한다.
   *
   * <p>문자열로 전달된 color, size는 각각 {@link ProductColor#of(String)}, {@link ProductSize#of(String)} 을
   * 통해 변환된다.
   *
   * @param color 상품 옵션의 색상
   * @param size 상품 옵션의 사이즈
   * @return 생성된 ProductVariant 객체
   */
  public static ProductVariant createProductVariant(String color, String size) {
    return ProductVariant.create(
        new ProductVariantCreateCommand(ProductColor.of(color), ProductSize.of(size)));
  }

  /**
   * 기본값이 채워진 {@link ProductCreateCommand}를 생성한다.
   *
   * <p>설정되는 기본 값:
   *
   * <ul>
   *   <li>HubId : 랜덤 UUID
   *   <li>VendorId : 랜덤 UUID
   *   <li>Price : 10,000원
   * </ul>
   *
   * @param type 생성할 상품 타입 (SIMPLE, OPTION 등)
   * @return 기본값이 설정된 ProductCreateCommand
   */
  public static ProductCreateCommand newProductCreateCommand(ProductType type) {
    return new ProductCreateCommand(
        HubId.of(UUID.randomUUID()), VendorId.of(UUID.randomUUID()), Price.of(10_000L), type);
  }

  /**
   * 기본값이 채워진 {@link ProductVariantCreateCommand}를 생성한다.
   *
   * <p>문자열로 전달된 color, size는 각각 Value Object로 변환된다.
   *
   * @param color 상품 옵션의 색상
   * @param size 상품 옵션의 사이즈
   * @return ProductVariantCreateCommand 객체
   */
  public static ProductVariantCreateCommand newProductVariantCreateCommand(
      String color, String size) {
    return new ProductVariantCreateCommand(ProductColor.of(color), ProductSize.of(size));
  }
}
