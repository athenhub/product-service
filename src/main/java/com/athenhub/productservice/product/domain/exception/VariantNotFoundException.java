package com.athenhub.productservice.product.domain.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

/**
 * 상품 옵션(ProductVariant)을 찾을 수 없을 때 발생하는 도메인 예외.
 *
 * <p>요청한 옵션 식별자(ProductVariantId)에 해당하는 옵션이 Product Aggregate 내부에 존재하지 않을 경우 {@code getVariant()}
 * 로직에서 던져진다.
 *
 * <p>주로 수정(update), 삭제(remove), 조회(getVariant) 과정에서 발생하며, 존재하지 않는 옵션에 대한 잘못된 접근을 명확하게 표현하는 예외이다.
 *
 * @see com.athenhub.productservice.product.domain.Product#getVariant
 * @author 김지원
 * @since 1.0.0
 */
public class VariantNotFoundException extends AbstractServiceException {

  /**
   * VariantNotFoundException 생성자.
   *
   * @param errorCode 에러 코드(enum 기반)
   * @param errorArgs 에러 메시지에 삽입될 추가 정보(상품 ID, 옵션 ID 등)
   */
  public VariantNotFoundException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
