package com.athenhub.productservice.product.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 상품의 판매 상태(ProductStatus)를 나타낸다.
 *
 * <p>각 상태는 상품이 현재 어떤 단계에 있는지를 표현하며, 판매 가능 여부나 화면 노출 여부 등을 결정하는 데 사용된다.
 *
 * <ul>
 *   <li>{@link #DRAFT} — 상품 등록 작업이 완료되지 않은 상태
 *   <li>{@link #ON_SALE} — 현재 판매 중인 상태
 *   <li>{@link #SOLD_OUT} — 재고가 모두 소진된 상태
 *   <li>{@link #DISCONTINUED} — 더 이상 판매하지 않는 단종 상태
 *   <li>{@link #HIDDEN} — 숨김 처리된 상태(관리자만 조회 가능)
 * </ul>
 *
 * <p>도메인 로직에서 상품 노출, 주문 가능 여부 판단 시 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum ProductStatus {

  /** 상품 등록 작업이 진행 중인 상태. */
  DRAFT("상품 등록 중"),

  /** 정상적으로 판매 중인 상태. */
  ON_SALE("상품 판매 중"),

  /** 재고가 모두 소진된 상태. */
  SOLD_OUT("상품 재고 없음"),

  /** 더 이상 판매하지 않는 단종 상태. */
  DISCONTINUED("상품 단종"),

  /** 사용자에게 노출되지 않는 숨김 상태. */
  HIDDEN("상품 숨김 처리"),

  /** 상품이 삭제된 상태. */
  DELETED("상품 삭제 처리");

  /** 상태 설명 (관리 UI 등에서 사용). */
  private final String description;
}
