package com.athenhub.productservice.product.domain;

/**
 * 상품의 사이즈(ProductSize)를 나타내는 열거형(enum)이다.
 *
 * <p>옵션 상품(ProductType.OPTION)에서 Variant를 구성할 때 사용되며, 일반적인 의류/잡화 등의 사이즈 체계를 표현한다.
 *
 * <ul>
 *   <li>{@link #SS}
 *   <li>{@link #S}
 *   <li>{@link #M}
 *   <li>{@link #L}
 *   <li>{@link #XL}
 *   <li>{@link #XXL}
 *   <li>{@link #FREE} 사이즈 옵션이 1개인 경우
 * </ul>
 *
 * <p>도메인 로직에서는 단순 값 객체(Value)로 취급되며, 사이즈 비교 또는 옵션 중복 여부 판단(isSameOption) 등에 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public enum ProductSize {
  SS,
  S,
  M,
  L,
  XL,
  XXL,
  FREE
}
