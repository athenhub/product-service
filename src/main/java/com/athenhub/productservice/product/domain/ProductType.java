package com.athenhub.productservice.product.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 상품의 유형(ProductType)을 나타낸다.
 *
 * <p>상품이 옵션을 가질 수 있는지 여부를 결정하며, Product 도메인에서 옵션 관련 기능(add/update/removeVariant)의 허용 여부를 판단할 때
 * 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Getter
public enum ProductType {

  /** 옵션이 존재하지 않는 단일 상품 */
  SIMPLE("옵션이 없는 상품"),

  /** 색상/사이즈 등의 옵션을 포함할 수 있는 상품 */
  OPTION("옵션이 있는 상품");

  /** 상품 유형의 설명(관리 UI 등에서 사용 가능) */
  private final String description;
}
