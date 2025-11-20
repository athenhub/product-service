package com.athenhub.productservice.product.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 상품의 색상(ProductColor)을 표현하는 열거형(enum)이다.
 *
 * <p>옵션 상품(ProductType.OPTION)의 Variant 구성 요소로 사용되며, 동일 색상 + 동일 사이즈 조합 검증(isSameOption) 등에 활용된다.
 *
 * <p>색상은 단순한 값(Value) 개념으로서, 도메인 내부에서 색상 비교 및 옵션 중복 여부 판단 시 사용된다.
 *
 * <ul>
 *   <li>{@link #WHITE} — 흰색
 *   <li>{@link #BLACK} — 검은색
 *   <li>{@link #RED} — 빨간색
 *   <li>{@link #BLUE} — 파란색
 *   <li>{@link #GREEN} — 초록색
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Getter
public enum ProductColor {
  WHITE,
  BLACK,
  RED,
  BLUE,
  GREEN,
}
