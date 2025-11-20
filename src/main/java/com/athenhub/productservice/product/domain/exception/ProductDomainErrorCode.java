package com.athenhub.productservice.product.domain.exception;

import com.athenhub.commoncore.error.ErrorCode;
import lombok.RequiredArgsConstructor;

/**
 * 상품(Product) 및 옵션(ProductVariant) 도메인에서 발생하는 에러 코드 정의.
 *
 * <p>서비스 계층 또는 전역 예외 처리기에서 사용되며, 각 도메인 예외(InvalidProductTypeException, VariantNotFoundException 등)가
 * 어떤 HTTP 상태 코드와 어떤 에러 코드를 반환해야 하는지 명확하게 설정한다.
 *
 * <p>■ ErrorCode 구성 요소
 *
 * <ul>
 *   <li>{@code status} — HTTP 상태 코드
 *   <li>{@code code} — 에러 식별 문자열(로그·API 응답용)
 * </ul>
 *
 * <p>■ 정의된 에러 코드
 *
 * <ul>
 *   <li>{@link #PRODUCT_VARIANT_NOT_FOUND} — 요청한 옵션이 존재하지 않을 때
 *   <li>{@link #PRODUCT_VARIANT_ALREADY_EXIST} — 동일한 옵션(Color+Size)이 이미 존재할 때
 *   <li>{@link #PRODUCT_VARIANT_NOT_SUPPORTED} — 상품 타입이 옵션을 허용하지 않을 때
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum ProductDomainErrorCode implements ErrorCode {

  /** 옵션을 찾을 수 없음. */
  PRODUCT_VARIANT_NOT_FOUND(404, "PRODUCT_NOT_FOUND"),

  /** 동일 옵션이 이미 존재함. */
  PRODUCT_VARIANT_ALREADY_EXIST(400, "PRODUCT_VARIANT_ALREADY_EXIST"),

  /** 옵션 타입이 아닌 상품에 옵션 작업을 시도함. */
  PRODUCT_VARIANT_NOT_SUPPORTED(400, "INVALID_PRODUCT_TYPE");

  /** HTTP 상태 코드. */
  private final int status;

  /** 에러 코드 문자열. */
  private final String code;

  @Override
  public int getStatus() {
    return this.status;
  }

  @Override
  public String getCode() {
    return this.code;
  }
}
