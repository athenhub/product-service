package com.athenhub.productservice.product.application.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

/**
 * Product 애플리케이션 계층에서 발생하는 모든 예외의 최상위 타입이다.
 *
 * <p>상품(Product) 관련 비즈니스 로직 수행 중 발생하는 예외를 공통적으로 감싸기 위한 클래스이며, 서비스 레이어에서 정의된 {@link ErrorCode}를 포함하여
 * 클라이언트로 전달된다.
 *
 * <p>주로 다음과 같은 상황에서 사용된다.
 *
 * <ul>
 *   <li>상품 조회 실패
 *   <li>상품 수정/삭제 권한 없음
 *   <li>유효하지 않은 상품 상태 요청
 * </ul>
 *
 * <p>이 예외는 {@link AbstractServiceException}을 상속받아, 공통 예외 처리(Global Exception Handler)에서 일관된 형식의 응답을
 * 생성할 수 있다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public class ProductServiceException extends AbstractServiceException {

  /**
   * ProductServiceException 생성자.
   *
   * @param errorCode 서비스 계층에서 정의된 에러 코드
   * @param errorArgs 에러 메시지 포맷에 사용될 가변 인자
   */
  public ProductServiceException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
