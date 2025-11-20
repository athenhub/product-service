package com.athenhub.productservice.product.domain.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

/**
 * 상품 옵션(ProductVariant)이 이미 존재할 때 발생하는 도메인 예외.
 *
 * <p>옵션 상품(ProductType.OPTION)에서 동일한 옵션(Color + Size 조합)을 중복으로 추가하려고 시도할 때, Product Aggregate Root
 * 내부의 {@code ensureVariantNotExists()} 검증 로직에 의해 던져진다.
 *
 * <p>이 예외는 "동일 옵션 생성 불가"라는 도메인 규칙 위반을 표현한다.
 *
 * @see com.athenhub.productservice.product.domain.Product#ensureVariantNotExists
 * @author 김지원
 * @since 1.0.0
 */
public class VariantAlreadyExistsException extends AbstractServiceException {

  /**
   * VariantAlreadyExistsException 생성자.
   *
   * @param errorCode 에러 코드(enum 기반)
   * @param errorArgs 에러 메시지에 포함될 추가 정보(예: 상품 ID, 옵션 정보 등)
   */
  public VariantAlreadyExistsException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
