package com.athenhub.productservice.product.domain.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

/**
 * 상품이 옵션(Variant)을 지원하지 않는 유형(ProductType)임에도 옵션 관련 작업(생성, 수정, 삭제 등)을 시도했을 때 발생하는 도메인 예외.
 *
 * <p>예를 들어, {@code ProductType.SIMPLE} 상품에 대해 옵션 추가(addVariant) 또는 기존 옵션 수정/삭제 작업을 시도하는 경우 이 예외가
 * 던져진다.
 *
 * <p>이 예외는 {@link com.athenhub.productservice.product.domain.Product Product} Aggregate Root 내부의
 * 도메인 규칙을 위반했을 때 발생하며, 주로 {@code ensureOptionType()} 검증 로직에 의해 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public class ProductVariantNotSupportedException extends AbstractServiceException {

  /**
   * ProductVariantNotSupportedException 생성자.
   *
   * @param errorCode 에러 코드(enum 기반)
   * @param errorArgs 에러 메시지 포맷에 삽입될 파라미터
   */
  public ProductVariantNotSupportedException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
