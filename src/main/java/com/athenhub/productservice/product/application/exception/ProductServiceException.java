package com.athenhub.productservice.product.application.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

public class ProductServiceException extends AbstractServiceException {

  public ProductServiceException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
